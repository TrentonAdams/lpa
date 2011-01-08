/**
 * This file is part of the LDAP Persistence API (LPA).
 *
 * Copyright Trenton D. Adams <lpa at trentonadams daught ca>
 *
 * LPA is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * LPA is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with LPA.  If not, see <http://www.gnu.org/licenses/>.
 *
 * See the COPYING file for more information.
 */
package ca.tnt.ldaputils.annotations.processing;

import ca.tnt.ldaputils.LdapManager;
import ca.tnt.ldaputils.annotations.LdapAttribute;
import ca.tnt.ldaputils.annotations.LdapEntity;
import ca.tnt.ldaputils.exception.LdapNamingException;
import ca.tnt.ldaputils.exception.LpaAnnotationException;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.ldap.LdapName;
import java.lang.reflect.Field;
import java.util.*;

/**
 * {@link IAnnotationHandler} implementation that processes LPA annotations for
 * the purpose of generating JNDI Attributes for the bind call.
 * <p/>
 * Created :  02/01/11 1:09 PM MST
 *
 * @author Trenton D. Adams
 */
@SuppressWarnings({"ClassWithoutNoArgConstructor"})
public class LdapEntityBinder extends LdapEntityHandler
{
    private LdapName dn;

    private Attributes attributes;

    private List<Attributes> attributesList;
    private List<LdapName> dnList;

    /**
     * Initializes the entity binder with the instance to be bound to ldap.
     *
     * @param entityInstance the {@link LdapEntity} annotated entityInstance
     *                       instance that needs to be bound to ldap.
     */
    public LdapEntityBinder(final Object entityInstance)
    {
        entity = entityInstance;
        attributes = new BasicAttributes();
        attributesList = new ArrayList<Attributes>(10);
        dnList = new ArrayList<LdapName>(10);
    }

    /**
     * Does nothing, and does not call the super, as the entity binder does not
     * require the handling of manager instances.
     */
    @SuppressWarnings({"RefusedBequest"})
    @Override
    protected void processManager(final Field field)
        throws IllegalAccessException
    {
    }

    @Override
    protected void processLdapAttributes(final Field field)
        throws IllegalAccessException
    {
    }

    /**
     * Processes the field for binding, generates a JNDI Attribute for it, and
     * puts it in the list of JNDI Attributes.
     *
     * @param field          the field being processed
     * @param attrAnnotation the {@link LdapAttribute} annotation instance being
     *                       processed
     *
     * @return always null, as we are not attempting to write to the instance,
     *         we're binding to LDAP using the instance values.
     */
    @Override
    protected Object processAttribute(final Field field,
        final LdapAttribute attrAnnotation)
        throws NamingException, IllegalAccessException
    {
        final Object returnValue = null;    // always null, we're reading
        try
        {
            field.setAccessible(true);

            final Class fieldType = field.getType();
            final String attrName = attrAnnotation.name();
            final Object fieldValue = field.get(entity);
            final Attribute attribute = new BasicAttribute(attrName);

            if (fieldValue != null)
            {
                if (isMultiValued(fieldType))
                {   // assumed to accept list of ALL attribute values
                    if (fieldValue instanceof Collection)
                    {
                        final Collection values = (Collection) fieldValue;
                        for (final Object value : values)
                        {
                            attribute.add(value);
                        }
                    }
                    else
                    {
                        throw new LpaAnnotationException(
                            "unsupported Class type; we plan on supporting " +
                                "TypeHandler for non aggregates in the future");
                    }
                }
                else
                {   // accepts single valued String attributes attribute
                    attribute.add(fieldValue);
                }
            }

            if (attribute.size() > 0)
            {
                attributes.put(attribute);
            }
        }
        finally
        {
            field.setAccessible(false);
        }
        return returnValue;
    }

    /**
     * Process the field as a foreign aggregate for binding, generates a JNDI
     * Attribute for it, and stores it in the list of JNDI Attributes
     * <p/>
     * CRITICAL binding annotation processing of aggregates
     * https://github.com/TrentonAdams/lpa/issues/issue/10
     *
     * @return always null, as we are not attempting to write to the instance,
     *         we're binding to LDAP using the instance values.
     */
    @Override
    protected Object processForeignAggregate(final Field field,
        final Class<?> aggClass,
        final String dnReference, final LdapAttribute attrAnnotation)
        throws NamingException, IllegalAccessException
    {
        final Object returnValue = null;    // always null, we're reading
        System.out.println(
            "attribute foreign aggregate field: " + field.getName() + ": " +
                field.get(entity));


        return returnValue;
    }

    /**
     * Process the field as a local aggregate for binding, generates a series of
     * JNDI Attribute for it, and stores them in the list of JNDI Attributes.
     * <p/>
     * Developer Note: Attributes of a local aggregate are to be part of the
     * main entry.  So, we simply copy them into the Attributes.  Local
     * aggregates are not allowed to have foreign aggregates inside of them.
     *
     * @return always null, as we are not attempting to write to the instance,
     *         we're binding to LDAP using the instance values.
     */
    @Override
    protected Object processLocalAggregate(final Field field,
        final Class<?> aggClass, final LdapAttribute attrAnnotation)
        throws IllegalAccessException, InstantiationException, NamingException
    {
        final Object fieldValue = null;

        final AnnotationProcessor annotationProcessor =
            new AnnotationProcessor();
        final LdapEntityBinder entityBinder;
        entityBinder = new LdapEntityBinder(field.get(entity));
        entityBinder.setManager(manager);
        annotationProcessor.addHandler(entityBinder);
        annotationProcessor.processAnnotations();

        final List<Attributes> attrList = entityBinder.getAttributesList();
        if (attrList.size() != 1)
        {
            throw new LdapNamingException(
                "local aggregate attribute list invalid: " + attrList.size());
        }
        final Attributes localAttributes = attrList.get(0);
        final NamingEnumeration attrEnum = localAttributes.getAll();
        while (attrEnum.hasMore())
        {
            attributes.put((Attribute) attrEnum.next());
        }

        return fieldValue;
    }

    /**
     * Only calls {@link LdapEntityLoader#validateDN(Class, Field)}, and then
     * stores the dn for return through the {@link #getDn()} method.
     */
    @SuppressWarnings({"RefusedBequest"})
    @Override
    protected void processDN(final Class annotatedClass, final Field field)
        throws IllegalAccessException, NoSuchMethodException
    {
        validateDN(annotatedClass, field);
        field.setAccessible(true);
        dn = (LdapName) field.get(entity);
        field.setAccessible(false);
    }

    /**
     * grabs the object classes from the {@link LdapEntity#requiredObjectClasses()}
     * annotation field, and puts them in an {@link Attribute}, and stores them
     * for return from {@link #getAttributesList()}
     */
    @SuppressWarnings({"RefusedBequest"})
    @Override
    protected boolean preProcessAnnotation(final LdapEntity annotation,
        final Class annotatedClass)
    {
        final Attribute attribute = new BasicAttribute("objectClass");
        for (final String objectClass : annotation.requiredObjectClasses())
        {
            attribute.add(objectClass);
        }

        if (attribute.size() > 0)
        {
            attributes.put(attribute);
        }

        return true;
    }

    @Override
    public void validateProcessing()
    {
    }

    @Override
    public void setManager(final LdapManager managerInstance)
    {
    }

    /**
     * Retrieve the processed attributes for LDAP.
     * <p/>
     * <span style="color:red;">WARNING! WARNING! WARNING!</span> you may only
     * call this method a SINGLE time. Once it has been called, the list of
     * Attributes objects is immutable, and this method will throw an {@link
     * UnsupportedOperationException}.
     *
     * @return the directory Attributes to be bound
     *
     * @throws UnsupportedOperationException if you call this method more than
     *                                       once
     */
    @SuppressWarnings(
        {"PublicMethodNotExposedInInterface", "ReturnOfCollectionOrArrayField"})
    public List<Attributes> getAttributesList()
        throws UnsupportedOperationException
    {
        attributesList.add(0, attributes);
        // list no longer modifiable
        attributesList = Collections.unmodifiableList(attributesList);
        return attributesList;
    }

    /**
     * Get the list of DNs that match the list of Attributes returned by {@link
     * #getAttributesList()}
     * <p/>
     * <span style="color:red;">WARNING! WARNING! WARNING!</span> you may only
     * call this method a SINGLE time. Once it has been called, the list of DNs
     * is immutable, and this method will throw an {@link
     * UnsupportedOperationException}.
     *
     * @return the dn list
     *
     * @throws UnsupportedOperationException if you call this method more than
     *                                       once
     */
    @SuppressWarnings(
        {"PublicMethodNotExposedInInterface", "ReturnOfCollectionOrArrayField"})
    public List<LdapName> getDnList()
    {
        dnList.add(0, dn);
        dnList = Collections.unmodifiableList(dnList);
        return dnList;
    }

    /**
     * Returns the dn processed during {@link #processDN(Class, Field)}
     *
     * @return the dn
     */
    @SuppressWarnings({"PublicMethodNotExposedInInterface"})
    public LdapName getDn()
    {
        return dn;
    }
}
