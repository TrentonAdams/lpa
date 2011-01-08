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
import ca.tnt.ldaputils.exception.LpaAnnotationException;

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
    }

    private Attributes attributes;

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
     *
     * @param field          the field being processed
     * @param aggClass       the aggregate class as defined by {@link
     *                       LdapAttribute#aggregateClass()}
     * @param dnReference    the "properly" formatted dn, with bind parameter,
     *                       as returned by the {@link LdapAttribute#referencedDNMethod()}
     *                       method
     * @param attrAnnotation the {@link LdapAttribute} annotation instance being
     *                       processed
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
     * Process the field as a local aggregate for binding, generates a JNDI
     * Attribute for it, and stores it in the list of JNDI Attributes
     *
     * @param field          the field being processed
     * @param aggClass       the aggregate class as defined by {@link
     *                       LdapAttribute#aggregateClass()}
     * @param attrAnnotation the {@link LdapAttribute} annotation instance being
     *                       processed
     *
     * @return always null, as we are not attempting to write to the instance,
     *         we're binding to LDAP using the instance values.
     */
    @Override
    protected Object processLocalAggregate(final Field field,
        final Class<?> aggClass, final LdapAttribute attrAnnotation)
        throws IllegalAccessException, InstantiationException
    {
        final Object returnValue = null;    // always null, we're reading
        System.out.println(
            "attribute local aggregate field: " + field.getName() + ": " +
                field.get(entity));
        return returnValue;
    }

    /**
     * Only calls {@link LdapEntityLoader#validateDN(Class, Field)}, and then
     * retrieves the dn for return thruogh the {@link #getDn()} method.
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
     * Retrieve the processed attributes for Ldap.
     *
     * @return the directory Attributes to be bound
     */
    @SuppressWarnings({"PublicMethodNotExposedInInterface"})
    public Attributes getAttributes()
    {
        return attributes;
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
