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
import ca.tnt.ldaputils.exception.LdapNamingException;
import org.apache.log4j.Logger;

import javax.naming.InvalidNameException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

/**
 * {@link IAnnotationHandler} implementation that processes LPA annotations for
 * the purpose of loading the {@link LdapEntity} annotated instance from the
 * ldap query results.
 * <p/>
 * This class was engineered in such a way; wait, back the truck up just a bit
 * (hehe). Let's face it, this class was HACKED together in such a way that
 * allows subclassing; DOC more about that later.
 * <p/>
 * Created :  22-Aug-2010 12:44:56 AM MST
 *
 * @author Trenton D. Adams
 */
@SuppressWarnings({"ProtectedField"})
public class LdapEntityLoader implements IAnnotationHandler
{
    private static final Logger logger = Logger.getLogger(
        LdapEntityLoader.class);
    /**
     * The {@link LdapEntity} annotated object instance.
     */
    protected Object entity;
    private boolean isDnSet;
    private Attributes attributes;
    private LdapName dn;
    private LdapManager manager;

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


    @SuppressWarnings({"ChainedMethodCall", "MethodWithMultipleReturnPoints"})
    @Override
    public boolean processAnnotation(final Annotation annotation,
        final Class annotatedClass)
    {
        try
        {
            final String className = entity.getClass().getName();

            if (!validateObjectClasses((LdapEntity) annotation
            ))
            {
                return false; // we're not loading anything, cause nothing is supported
            }

            for (final Field field : annotatedClass.getDeclaredFields())
            {   // BEGIN field iteration
                if (field.isAnnotationPresent(Manager.class))
                {
                    processManager(field);
                }

                if (field.isAnnotationPresent(DN.class))
                {
                    logger.debug(String.format("%-20s annotation on ", "@DN") +
                        className + ':' + field.getName());
                    processDN(annotatedClass, field);
                }

                if (field.isAnnotationPresent(LdapAttribute.class))
                {   // BEGIN LdapAttribute annotation processing
                    logger.debug(String.format("%-20s annotation on ",
                        "@LdapAttribute") + className + ':' + field.getName());
                    processLdapAttribute(annotatedClass, field);
                }   // END LdapAttribute annotation processing
            }   // END field iteration

        }
        catch (NamingException e)
        {
            throw new LdapNamingException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new IllegalArgumentException(e);
        }
        catch (NoSuchMethodException e)
        {
            throw new IllegalArgumentException(annotatedClass.getName() +
                " is not a valid LdapEntity POJO; method incorrectly defined " +
                "or does not exist", e);
        }
        catch (InstantiationException e)
        {
            throw new IllegalArgumentException(e);
        }
        catch (InvocationTargetException e)
        {
            throw new IllegalArgumentException(annotatedClass.getName() +
                " is not a valid LdapEntity POJO; method incorrectly defined",
                e);
        }

        return true;
    }

    /**
     * Validates object classes for the entity.
     * <p/>
     * Override for a subclassed annotation processor.  In some cases, just
     * return true for the subclass, if you have nothing to do.
     *
     * @param annotation the {@link LdapEntity} annotation instance for the
     *                   class
     *
     * @return whether the object classes are valid or not
     */
    protected boolean validateObjectClasses(final LdapEntity annotation)
    {
        final Attribute objectClass = attributes.get("objectClass");
        final String[] supportedClasses =
            ((LdapEntity) annotation).requiredObjectClasses();
        boolean hasSupportedClass = true;
        for (final String supportedClass : supportedClasses)
        {
            hasSupportedClass = hasSupportedClass && objectClass.contains(
                supportedClass);
        }
        return hasSupportedClass;
    }

    /**
     * Handles injecting the manager.
     *
     * @param field the field to inject the manager to
     *
     * @throws IllegalAccessException if we are not allowed access due to java
     *                                policies
     */
    protected void processManager(final Field field)
        throws IllegalAccessException
    {
        field.setAccessible(true);
        field.set(entity, manager);
        field.setAccessible(false);
    }

    /**
     * Processing for {@link LdapAttribute } annotation where name is '*'
     *
     * @param field the field the annotation is on
     *
     * @throws IllegalAccessException obvious
     */
    protected void processLdapAttributes(final Field field)
        throws IllegalAccessException
    {
        field.setAccessible(true);
        field.set(entity, attributes);
        field.setAccessible(false);
    }

    /**
     * Processing for {@link DN } annotation
     *
     * @param annotatedClass class of the annotated object
     * @param field          the field the annotation is on
     *
     * @throws IllegalAccessException obvious
     * @throws NoSuchMethodException  if the getter for the field does not exist
     *                                in the annotated object
     */
    protected void processDN(final Class annotatedClass, final Field field)
        throws IllegalAccessException, NoSuchMethodException
    {
        field.setAccessible(true);
        field.set(entity, dn.clone());
        field.setAccessible(false);
        validateDN(annotatedClass, field);
    }

    /**
     * Validates that the class is annotated with {@link DN}
     *
     * @param annotatedClass the {@link LdapEntity} annotated class
     * @param field          the field annotated with DN
     *
     * @throws NoSuchMethodException if the getter for the field does not exist
     *                               in the annotated object
     */
    protected void validateDN(final Class annotatedClass, final Field field)
        throws NoSuchMethodException
    {
        final String methodName;    // final name of method
        final String fieldName;
        final String firstChar;     // first char of field name
        final String endOfField;    // field name minus first character
        final Method dnGetMethod;

        fieldName = field.getName();
        firstChar = fieldName.substring(0, 1);
        endOfField = fieldName.length() > 1 ? fieldName.substring(1) : "";
        methodName = "get" + firstChar.toUpperCase() + endOfField;
        dnGetMethod = annotatedClass.getMethod(methodName);
        if (!Modifier.isPublic(dnGetMethod.getModifiers()) ||
            !LdapName.class.equals(dnGetMethod.getReturnType()))
        {   // not defined to return an LdapName or not public
            throw new NoSuchMethodException(methodName +
                " is not defined correctly.  It must be a public " +
                "no args method, returning an LdapName");
        }

        isDnSet = true;
    }

    /**
     * Only to be used if the field needs to be injected.  If you're doing
     * read-only processing for your subclass of LdapEntityLoader, then override
     * this, and have it do nothing.
     *
     * @param field      the field
     * @param fieldValue the value of the field to inject
     *
     * @throws IllegalAccessException if java policies prevent access
     */
    private void injectField(final Field field, final Object fieldValue)
        throws IllegalAccessException
    {
        if (fieldValue != null)
        {   // never set to null, as the contructor may have initialized
            // a default empty collection or something.
            field.set(entity, fieldValue);
        }
    }

    /**
     * Simply determines if this is a multi valued field.  We assume it is if it
     * is not either a String or a byte array.  These are the only supported
     * return types for attribute values from Sun's LDAP provider.
     * <p/>
     * REQUIRED_FEATURE support other LDAP providers https://github.com/TrentonAdams/lpa/issues/7
     * We'll have to figure something else out if we want to work with other
     * LDAP JNDI service providers in the future.
     *
     * @param fieldType the field's type
     *
     * @return true if it is a multi valued field, false otherwise
     */
    private static boolean isMultiValued(final Class fieldType)
    {
        return !String.class.equals(fieldType) &&
            !(byte.class.equals(fieldType) && fieldType.isArray());
    }

    /**
     * Processing for {@link LdapAttribute } annotation.  If the LdapEntity
     * annotated Class is an instanceof {@link TypeHandler}, the {@link
     * TypeHandler#processValues(List, Class)} will be called for all aggregate
     * fields, instead of the normal type processing that goes on, described by
     * {@link LdapAttribute}
     * <p/>
     * IMPORTANT FEATURE we need to support ALL types of attribute types,
     * including images and what not.  We do this via the TypeHandler, by
     * putting "raw" types, directly into the list, before passing it to the
     * type handler implementation.
     *
     * @param annotatedClass the class of the annotated object
     * @param field          the field the annotation is on
     *
     * @throws IllegalAccessException    obvious
     * @throws InvocationTargetException obvious
     * @throws NoSuchMethodException     if the referenceDNMethod is the name of
     *                                   a method that does not exist
     * @throws NamingException           obvious
     * @throws InstantiationException    obvious
     * @throws ClassCastException        if you use a supported collection that
     *                                   is not parameterized, such as
     *                                   SortedSet, instead of SortedSet&lt;String&gt;,
     *                                   for example.
     */
    @SuppressWarnings({"MethodWithMultipleReturnPoints", "unchecked"})
    protected void processLdapAttribute(final Class annotatedClass,
        final Field field) throws IllegalAccessException, NamingException,
        InvocationTargetException, NoSuchMethodException, InstantiationException
    {   // BEGIN processLdapAttribute()
        field.setAccessible(true);

        Object fieldValue = null;
        final LdapAttribute attrAnnotation = field.getAnnotation(
            LdapAttribute.class);
        if ("*".equals(attrAnnotation.name()))
        {   // all attributes stored in field
            processLdapAttributes(field);
            return;
        }
        final String referenceDNMethod = attrAnnotation.referencedDNMethod();
        final Class<?> aggClass = attrAnnotation.aggregateClass();
        final Class refType = field.getType();

        try
        {
            final boolean isAggregate = !Object.class.equals(aggClass);
            if (!isAggregate)
            {   // regular string or byte array attributes in a collection, or
                // by themselves.
                fieldValue = processAttribute(refType, attrAnnotation);
            }
            else
            {   // an aggregate, attribut must be a string
                fieldValue = processAggregate(annotatedClass,
                    referenceDNMethod, aggClass, refType, attrAnnotation);
            }

            injectField(field, fieldValue); // may do nothing
        }
        finally
        {   // reset java language checks
            field.setAccessible(false);
        }
    }   // END processLdapAttribute()

    /**
     * Simple attribute processing.  Called when we're doing with attributes
     * only, and not aggregates.
     *
     * @param fieldType      the type of the field
     * @param attrAnnotation the LdapAttribute annotation instance
     *
     * @return the value for the field
     *
     * @throws NamingException if a jndi error occurs
     */
    @SuppressWarnings({"MethodWithMultipleReturnPoints", "unchecked"})
    private Object processAttribute(final Class fieldType,
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

    /**
     * Processes aggregates fields, which need to create completely different
     * objects, based on either another ldap entry entirely (foreign), or the
     * current ldap entry (local).
     * <p/>
     * Override this for another IAnnotationHandler.
     *
     * @param annotatedClass    the class that was annotated
     * @param referenceDNMethod the dn method of the instance class that can
     *                          obtain the dn with bind parameter ('?')
     * @param aggClass          the class of the aggregate
     * @param fieldType         the field type
     * @param attrAnnotation    the LdapAttribute annotation instance.
     *
     * @return the new aggregate, or collection of aggregates
     *
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws NamingException
     */
    @SuppressWarnings({"MethodWithMultipleReturnPoints", "unchecked"})
    protected Object processAggregate(final Class annotatedClass,
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
     * Do what you need to for the local aggregate.
     * <p/>
     * A local aggregate is an aggregate object which will be injected into the
     * object field, which has requested it via {@link LdapAttribute#aggregateClass()},
     * and is also using the existing LDAP entry's attributes as a basis for the
     * object.  See the documentation on {@link LdapAttribute#aggregateClass()}
     * for more information.
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

    /**
     * Retrieves the referenced dn, given the dnReference (see {@link
     * LdapAttribute#referencedDNMethod()} for more information) and the
     * attributeValue, and creates a new LdapEntity object.
     *
     * @param entityClass    the java class of the LdapEntity
     * @param dnReference    the dn reference with a '?' bind parameter
     * @param attributeValue the value of the attribute to escape, and inject
     *                       into dnReference before conversion to an LdapName
     *
     * @return the LdapEntity object
     *
     * @throws InvalidNameException if an error occurs creating the LdapName.
     */
    protected Object getReferencedEntity(final Class entityClass,
        final String dnReference, final Object attributeValue)
        throws InvalidNameException
    {
        final String dnLocalReference = dnReference.replace("?",
            Rdn.escapeValue(attributeValue));
        final LdapName ldapName = new LdapName(dnLocalReference);
        return manager.find(entityClass, ldapName);
    }

    @Override
    public Class getAnnotatedClass()
    {
        return entity.getClass();
    }

    @Override
    public Class<? extends Annotation> getAnnotationClass()
    {
        return LdapEntity.class;
    }

    @Override
    public void noAnnotation(final Class annotatedClass)
    {
        if (annotatedClass == entity.getClass())
        {   // top level class required to be annotated.
            throw new IllegalArgumentException(
                annotatedClass.getName() +
                    " is not a valid LdapEntity POJO; @LdapEntity" +
                    " annotation REQUIRED");
        }
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
}
