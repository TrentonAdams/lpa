package ca.tnt.ldaputils.impl;

import ca.tnt.ldaputils.ILdapEntry;
import ca.tnt.ldaputils.annotations.*;
import ca.tnt.ldaputils.exception.LdapNamingException;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;

import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.LdapName;
import java.util.*;

/**
 * Default implementation of an LDAP object.  This handles a fair bit of LDAP
 * update capability, by way of using the factory.
 * <p/>
 * Created :  16-Aug-2010 8:05:16 PM MST
 * <p/>
 * Modified : $Date$ UTC
 * <p/>
 * Revision : $Revision$
 *
 * @author Trenton D. Adams
 */
@LdapEntity
public class LdapEntry implements ILdapEntry, Comparable
{
    private static final Logger logger = Logger.getLogger(LdapEntry.class);
    protected boolean modified;
    protected boolean isNew;

    protected LinkedHashMap modificationItems;

    @Manager
    private ca.tnt.ldaputils.LdapManager manager;

    /**
     * This contains all of the attributes for the object
     */
    @LdapAttribute(name = "*")
    protected Attributes attributes;

    /**
     * LDAP Distinguished Name
     */
    @DN
    protected LdapName dn;

    @LdapAttribute(name = "cn")
    protected String cn;

    /**
     * All objectClass attributes, we know they are Strings
     */
    @LdapAttribute(name = "objectClass")
    protected List<String> objectClasses;

/*    @Factory
    private LDAPFactory factory;*/

    public LdapEntry()
    {
        modificationItems = new LinkedHashMap();
    }

    /**
     * @return the distinquished name of the object. ie, fully qualified path in
     *         LDAP tree.
     */
    public LdapName getDn()
    {
        return dn;
    }

    @Override
    public String getDescription()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ILdapEntry convertInstance(final int type) throws NamingException
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Converts this object to the given instance.
     *
     * @param classType the object type to convert to.  MUST have been annotated
     *                  with {@link LdapEntry}
     *
     * @return the new object.
     *
     * @throws NamingException if conversion fails due to the proper objectClass
     *                         not being setup.
     */
/*
    CRITICAL must implement a convertInstance
public Object convertInstance(Class classType) throws NamingException
    {
        return LDAPFactory.createInstance(attributes, clas, dn);
    }*/
    public String getStringAttribute(final Attributes attributes,
        final String attribute) throws NamingException
    {
        final Attribute temp;
        final String attributeValue;
        temp = attributes.get(attribute);
        if (temp != null)
        {
            attributeValue = (String) temp.get();
            logger.debug(attribute + ": " + getStringValue("cn"));
        }
        else
        {
            attributeValue = null;
        }

        return attributeValue;
    }

    public List getAttributeValues(final String attribute)
    {
        final Attribute ldapAttribute;
        List values = null;

        ldapAttribute = attributes.get(attribute);
        try
        {
            if (ldapAttribute != null && ldapAttribute.size() != 0)
            {
                values = Collections.list(ldapAttribute.getAll());
            }
        }
        catch (NamingException e)
        {
            throw new LdapNamingException(e);
        }

        return values;
    }

    public String getStringValue(final String attribute)
    {
        final Attribute ldapAttribute;
        try
        {
            ldapAttribute = attributes.get(attribute);
            if (ldapAttribute != null && ldapAttribute.size() != 0)
                return (String) ldapAttribute.get(0);
            else
            {
                return null;
            }
        }
        catch (NamingException e)
        {
            throw new LdapNamingException(e);
        }

    }

    /**
     * Sets the given attribute right now, and does not delay.  This should only
     * be used in the case where there is only one value for the attribute. If
     * there are multiple values, then the modifyBatchAttribute is the one that
     * really needs to be called.  If you call this with REPLACE_ATTRIBUTE for
     * instance, and there was multiple entries in LDAP, then the existing
     * entries will be replaced with this one value and only this one value. In
     * addition, if you want to modify multiple attributes at a time, then you
     * should not call this, you should use modifyBatchAttribute().
     *
     * @param operation on of ADD_ATTRIBUTE, REPLACE_ATTRIBUTE,
     *                  REMOVE_ATTRIBUTE
     * @param attribute the name of the attribute
     * @param value     the value of the attribute
     *
     * @see #ADD_ATTRIBUTE ADD_ATTRIBUTE
     * @see #REPLACE_ATTRIBUTE REPLACE_ATTRIBUTE
     * @see #REMOVE_ATTRIBUTE REMOVE_ATTRIBUTE
     */
    public void modifyAttribute(final int operation, final String attribute,
        final Object value
    )
    {
        modifyBatchAttribute(operation, attribute, value);
        modifyBatchAttributes();   // run the attribute operation
    }

    /**
     * Please note, the preferred method is to call setXXXX() where XXXX is the
     * attribute name, followed by save().
     * <p/>
     * This sets a batch attribute.  This means that it will be added to a queue
     * for changing LDAP.  You can modify the same attribute multiple times,
     * assuming LDAP supports multivalued attributes for that attribute. You are
     * then required to call modifyBatchAttributes(), which will actually do the
     * operations requested.
     * <p/>
     * You should call this one or more times per attribute, followed by
     * modifyBatchAttributes().
     * <p/>
     * Each time you call this method, for the same attribute, you should
     * specify the same operation, otherwise you will get an
     * IllegalArgumentException, with an appropriate error message.
     *
     * @param operation one of ADD_ATTRIBUTE, REPLACE_ATTRIBUTE,
     *                  REMOVE_ATTRIBUTE
     * @param attribute the name of the attribute
     * @param value     the value of the attribute
     *
     * @see #ADD_ATTRIBUTE ADD_ATTRIBUTE
     * @see #REPLACE_ATTRIBUTE REPLACE_ATTRIBUTE
     * @see #REMOVE_ATTRIBUTE REMOVE_ATTRIBUTE
     */
    public void modifyBatchAttribute(final int operation,
        final String attribute, final Object value
    )
    {
        final Attribute newAttribute;
        ModificationItem modItem;
        final int mod_op;

        switch (operation)
        {
            case ADD_ATTRIBUTE:
                mod_op = DirContext.ADD_ATTRIBUTE;
                break;
            case REPLACE_ATTRIBUTE:
                mod_op = DirContext.REPLACE_ATTRIBUTE;
                break;
            case REMOVE_ATTRIBUTE:
                mod_op = DirContext.REMOVE_ATTRIBUTE;
                break;
            default:
                mod_op = DirContext.ADD_ATTRIBUTE;
        }

        modItem = (ModificationItem) modificationItems.get(attribute);
        if (modItem == null)
        {   // first time we are doing something with this attribute
            newAttribute = new BasicAttribute(attribute, value);
            modItem = new ModificationItem(mod_op, newAttribute);
        }
        else
        {   // we will add it to the attribute values for this attribute
            if (modItem.getModificationOp() != mod_op)
            {   // make sure they aren't changing their mind on which op
                throw new IllegalArgumentException(
                    "error, operation does not match previous batch items for this attribute");
            }

            modItem.getAttribute().add(value);
        }
        modified = true;
        modificationItems.put(attribute, modItem);
    }

    /**
     * Runs the batch modifications requested through the {@link
     * ILdapEntry#modifyBatchAttribute(int, String, Object)}
     */
    public void modifyBatchAttributes()
    {   // BEGIN modifyBatchAttributes()
        DirContext ldapContext = null;

        if (modificationItems.size() == 0)
        {
            throw new IllegalStateException("No modification items for batch");
        }
        try
        {
            final Object[] tempModItems;
            final ModificationItem[] modItems;
            tempModItems = modificationItems.values().toArray();
            modItems = new ModificationItem[tempModItems.length];
            for (int index = 0; index < tempModItems.length; index++)
            {   // convert to ModificationItem array
                modItems[index] = (ModificationItem) tempModItems[index];
            }

            ldapContext = manager.getConnection();
            ldapContext.modifyAttributes(dn, modItems);

            /**
             * Update the attributes in memory
             */
            for (final ModificationItem modItem : modItems)
            {
                final Attribute attribute;
                attribute = modItem.getAttribute();
                updateAttribute(attribute.getID());
            }
        }
        catch (NamingException namingException)
        {
            throw new LdapNamingException(namingException);
        }
        catch (Exception exception)
        {
            throw new LdapNamingException("error modifying attributes",
                exception);
        }
        finally
        {
            try
            {
                if (ldapContext != null)
                {
                    ldapContext.close();
                }
            }
            catch (NamingException namingException)
            {
                manager.logNamingException(namingException);
            }

            // recreate empty batch list
            modificationItems = new LinkedHashMap();
        }
    }   // END modifyBatchAttributes()

    /**
     * Because LDAP operations are expensive, we have a save method.  Saves any
     * changes made by setXXXX() methods, where XXXX is an attribute name.  Also
     * an alias for modifyBatchAttributes(), but will do nothing unless
     * modifyBatchAtribute() has been called
     */
    public void save()
    {
        if (modified)
        {
            modified = false;
            modifyBatchAttributes();
        }
    }

    @Override
    public Attributes getBindAttributes()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Updates the specified attribute from LDAP.
     * <p/>
     * TODO : Instead of using LDAPFactory.getAttributes, using
     * DirContext.getAttributes().  Then we can remove the getAttributes().
     *
     * @param attrName the name of the attribute
     *
     * @throws NamingException if any LDAP errors occur.
     */
    protected void updateAttribute(final String attrName) throws NamingException
    {
        final String[] returningAttributes;
        final Attributes returnedAttributes;

        returningAttributes = new String[1];
        returningAttributes[0] = attrName;
        returnedAttributes = manager.getAttributes(dn, returningAttributes);

        if (returnedAttributes.size() == 1)
        {   // only attempt to load the attributes if the search found them.
            // the attribute to update
            attributes.put(returnedAttributes.get(attrName));
        }
    }

    public Attributes getAttributes()
    {
        return attributes;
    }

    public List<String> getObjectClasses()
    {
        return objectClasses;
    }

    @Override
    public String getCN()
    {
        return cn;
    }

    public void setObjectClasses(final List<String> objectClasses)
    {
        this.objectClasses = objectClasses;
    }

    public boolean isObjectClass(final String objectClass)
    {
        return objectClasses.contains(objectClass);
    }

    @Override
    public String toString()
    {
        return "LdapEntry{" +
            "dn=" + dn +
            ", cn='" + cn + '\'' +
            '}';
    }

    @SuppressWarnings({"ChainedMethodCall", "NonFinalFieldReferenceInEquals"})
    @Override
    public boolean equals(final Object o)
    {
        final LdapEntry rhs = (LdapEntry) o;

        return new EqualsBuilder()
            .appendSuper(super.equals(o))
            .append(dn, rhs.dn)
            .append(cn, rhs.cn)
            .append(objectClasses, rhs.objectClasses)
            .isEquals();
    }

    @SuppressWarnings(
        {"ChainedMethodCall", "NonFinalFieldReferencedInHashCode"})
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37).
            append(dn).
            append(cn).
            append(objectClasses).
            toHashCode();
    }

    @SuppressWarnings({"ChainedMethodCall", "CompareToUsesNonFinalVariable"})
    @Override
    public int compareTo(final Object o)
    {
        final LdapEntry myClass = (LdapEntry) o;
        return new CompareToBuilder()
            .append(dn, myClass.dn)
            .append(cn, myClass.cn)
            .append(objectClasses, myClass.objectClasses)
            .toComparison();
    }
}
