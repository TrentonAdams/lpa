/**
 * This file is part of the Ldap Persistence API (LPA).
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
import ca.tnt.ldaputils.annotations.*;
import ca.tnt.ldaputils.exception.LpaAnnotationException;
import org.apache.log4j.Logger;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.ldap.LdapName;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

/**
 * {@link IAnnotationHandler} implementation that processes LPA annotations for
 * the purpose of loading the {@link LdapEntity} annotated instance from the
 * ldap query results.
 * <p/>
 * Created :  22-Aug-2010 12:44:56 AM MST
 *
 * @author Trenton D. Adams
 */
@SuppressWarnings({"ProtectedField"})
public class LdapEntityLoader extends LdapEntityHandler
{
    private static final Logger logger = Logger.getLogger(
        LdapEntityLoader.class);
    private boolean isDnSet;
    private Attributes attributes;
    private LdapName dn;

    /**
     * does nothing
     */
    public LdapEntityLoader()
    {
    }

    /**
     * creates a new LdapEntityHandler with given parameters.
     *
     * @param newObject  the new object to process
     * @param attributes the attributes from ldap
     * @param dn         the ldap distinguished name
     */
    public LdapEntityLoader(final Object newObject,
        final Attributes attributes, final LdapName dn)
    {
        entity = newObject;
        this.attributes = attributes;
        this.dn = dn;
    }

    @Override
    public void setManager(final LdapManager managerInstance)
    {
        manager = managerInstance;
    }

    @Override
    protected boolean preProcessAnnotation(final Annotation annotation,
        final Class annotatedClass)
    {
        return validateObjectClasses((LdapEntity) annotation, attributes);
    }

    @Override
    protected void processLdapAttributes(final Field field)
        throws IllegalAccessException
    {
        field.setAccessible(true);
        field.set(entity, attributes);
        field.setAccessible(false);
    }

    @Override
    protected void processDN(final Class annotatedClass, final Field field)
        throws IllegalAccessException, NoSuchMethodException
    {
        field.setAccessible(true);
        field.set(entity, dn.clone());
        field.setAccessible(false);
        isDnSet = validateDN(annotatedClass, field);
    }

    @Override
    @SuppressWarnings({"MethodWithMultipleReturnPoints", "unchecked"})
    protected Object processAttribute(final Field field, final Class fieldType,
        final LdapAttribute attrAnnotation)
        throws NamingException
    {
        // CRITICAL verify what happens if say an image were the
        // attribute value.  It seems like this probably wouldn't work.
        // Apparently sun's implementation only has String or byte array
        // attribute values.  However, other service providers potentially
        // provide other types.  For now, we need to check for "byte[]" as a
        // special case.  Later we can think about what we want to do with other
        // cases
        // http://download.oracle.com/javase/jndi/tutorial/ldap/misc/attrs.html
        //
        // CRITICAL we need to do TypeHandler calls for unknown types
        final String attrName = attrAnnotation.name();
        final Attribute attr = attributes.get(attrName);
        final NamingEnumeration attrValues =
            attr != null ? attr.getAll() : null;
        Object fieldValue = null;
        if (attr != null)
        {
            if (isMultiValued(fieldType))
            {   // assumed to accept list of ALL attribute values
                if (List.class.equals(fieldType))
                {
                    fieldValue = Collections.list(attrValues);
                }
                else if (SortedSet.class.equals(fieldType))
                {
                    // This is inefficient, but we don't care,
                    // because the chance of an LDAP entry having
                    // hundreds of values in a single attribute is
                    // SLIM to NONE.
                    fieldValue = new TreeSet(Collections.list(attrValues));
                }
            }
            else
            {   // accepts single valued String attributes attribute
                fieldValue = attr.get();
            }
        }
        return fieldValue;
    }

    @Override
    @SuppressWarnings({"MethodWithMultipleReturnPoints", "unchecked"})
    protected Object processAggregate(Field field, final Class annotatedClass,
        final String referenceDNMethod, final Class<?> aggClass,
        final Class fieldType, final LdapAttribute attrAnnotation)
        throws InstantiationException, IllegalAccessException,
        NoSuchMethodException, InvocationTargetException, NamingException
    {
        final String attrName = attrAnnotation.name();
        final Attribute attr = attributes.get(attrName);
        final NamingEnumeration attrValues =
            attr != null ? attr.getAll() : null;
        final Object fieldValue;
        // request to inject an LdapEntity from another LDAP entry
        // or use the existing ldap entry to grab Auxiliary attributes

        // CRITICAL finish refactoring this if needed.  We might want to process
        // local aggregates and foreign aggregates in subclasses.  So, this
        // processAggregate() might be best served as a concrete implementation
        // in LdapeEntityHandler

        // local aggregates are loaded from the current ldap entry
        final boolean isLocalAggregate = "".equals(referenceDNMethod);
        if (isLocalAggregate)
        {   // use current ldap entry for population of aggregate
            fieldValue = processLocalAggregate(aggClass);
        }
        else
        {   // BEGIN foreign ldap entry processing for aggregate
            final Method dnReferenceMethod = annotatedClass.getMethod(
                referenceDNMethod);
            final String dnReference =
                (String) dnReferenceMethod.invoke(entity);
            if (!dnReference.contains("?"))
            {
                throw new LpaAnnotationException(dnReference +
                    " is an invalid dynamic reference to an LDAP entry, " +
                    "it does not contain a replaceable parameter marked " +
                    "with '?'");
            }

            if (attr == null)
            {   // FEATURE perhaps we want the option of exception or silent failure?
                // FEATURE perhaps we want the option of required vs not required, and in combination with the silent feature?
                return null;
            }
            if (fieldType.equals(aggClass))
            {   // field not a collection of any kind, but is a
                // single object type of the aggClass.
                fieldValue = getReferencedEntity(aggClass, dnReference,
                    attr.get());
            }
            else
            {   // BEGIN handling collection of aggregates.

                final List ldapEntities = loadAggregates(aggClass,
                    attrValues, dnReference);

                if (fieldType.isArray())
                {   // convert to the array type used in the field
                    final Object refArray = Array.newInstance(
                        fieldType.getComponentType(),
                        ldapEntities.size());
                    fieldValue = ldapEntities.toArray(
                        (Object[]) refArray);
                }
                else if (List.class.equals(fieldType))
                {   // simply unordered List
                    fieldValue = ldapEntities;
                }
                else if (SortedSet.class.equals(fieldType))
                {   // sorted, Comparable on objects required
                    fieldValue = new TreeSet(ldapEntities);
                }
                else if (entity instanceof TypeHandler)
                {
                    fieldValue = ((TypeHandler) entity).processValues(
                        ldapEntities, fieldType);
                }
                else
                {
                    throw new LpaAnnotationException(
                        "unhandled field type: " + fieldType);
                }
            }   // END handling collection of aggregates.
        }   // END foreign ldap entry processing for aggregate
        return fieldValue;
    }

    /**
     * Loads all of the aggregates for the specific attribute values.  This is
     * done by injecting an Rdn escaped value from attrValues into the
     * dnReference.
     *
     * @param aggClass    aggregate class
     * @param attrValues  values of the attribute that is used to reference the
     *                    aggregate
     * @param dnReference the dn reference where the attrValues get injected
     *                    into, to replace the '?'
     *
     * @return a List of all the aggregates retrieved from other LDAP entries.
     *
     * @throws NamingException if an error occurs iterating the attrValues
     */
    @SuppressWarnings({"unchecked"})
    private List loadAggregates(final Class<?> aggClass,
        final NamingEnumeration attrValues, final String dnReference)
        throws NamingException
    {
        final List ldapEntities = new ArrayList();
        while (attrValues.hasMore())
        {   // iterate through all ldap attributes
            // construct new LdapEntity objects along the way
            final Object valueObject = attrValues.next();
            ldapEntities.add(getReferencedEntity(aggClass,
                dnReference, valueObject));
        }
        return ldapEntities;
    }

    @SuppressWarnings({"ChainedMethodCall"})
    @Override
    public void validateProcessing()
    {
        if (!isDnSet())
        {   // an object in the tree must store the DN
            throw new IllegalArgumentException(
                entity.getClass().getName() +
                    " is not a valid LdapEntity POJO; @DN annotation " +
                    "REQUIRED");
        }
    }

    /**
     * Checks if the distinguished name was set
     *
     * @return true if the distinguished name was processed and set, false
     *         otherwise.
     */
    protected boolean isDnSet()
    {
        return isDnSet;
    }

    /**
     * Do what you need to for the local aggregate.
     * <p/>
     * A local aggregate is an aggregate object which will be injected into the
     * object field, which has requested it via {@link LdapAttribute#aggregateClass()},
     * and is also using the existing LDAP entry's attributes as a basis for the
     * object.  See the documentation on {@link LdapAttribute#aggregateClass()}
     * for more information.
     * <p/>
     *
     * @param aggClass the aggregate class, if needed.
     *
     * @return the new fieldValue if one is needed
     *
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    protected Object processLocalAggregate(final Class<?> aggClass)
        throws IllegalAccessException, InstantiationException
    {
        final Object fieldValue;
        fieldValue = aggClass.newInstance();
        final AnnotationProcessor annotationProcessor =
            new AnnotationProcessor();
        final LdapEntityLoader entityLoader;
        entityLoader = new LdapEntityLoader(fieldValue, attributes,
            dn);
        entityLoader.setManager(manager);
        annotationProcessor.addHandler(entityLoader);
        annotationProcessor.processAnnotations();

        return fieldValue;
    }
}
