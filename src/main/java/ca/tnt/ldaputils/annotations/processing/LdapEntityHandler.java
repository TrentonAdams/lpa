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
 * IAnnotationHandler
 * <p/>
 * Created :  22-Aug-2010 12:44:56 AM MST
 * <p/>
 * Modified : $Date$ UTC
 * <p/>
 * Revision : $Revision$
 *
 * @author Trenton D. Adams
 */
public class LdapEntityHandler implements IAnnotationHandler
{
    private static final Logger logger = Logger.getLogger(
        LdapEntityHandler.class);
    private Object object;
    private boolean isDnSet;
    private Attributes attributes;
    private LdapName dn;
    private LdapManager manager;

    /**
     * does nothing
     */
    public LdapEntityHandler()
    {
    }

    /**
     * creates a new LdapEntityHandler with given parameters.
     *
     * @param newObject  the new object to process
     * @param attributes the attributes from ldap
     * @param dn         the ldap distinguished name
     */
    public LdapEntityHandler(final Object newObject,
        final Attributes attributes, final LdapName dn)
    {
        object = newObject;
        this.attributes = attributes;
        this.dn = dn;
    }

    /**
     * Sets the established ldap manager object, which should be
     * pre-authenticated.
     *
     * @param manager the already authenticated manager
     */
    public void setManager(final LdapManager manager)
    {
        this.manager = manager;
    }


    @SuppressWarnings({"ChainedMethodCall", "MethodWithMultipleReturnPoints"})
    @Override
    public boolean processAnnotation(final Annotation annotation,
        final Class annotatedClass)
    {
        try
        {
            final Attribute objectClass = attributes.get("objectClass");
            final String className = object.getClass().getName();
            boolean hasSupportedClass = true;
            final String[] supportedClasses =
                ((LdapEntity) annotation).requiredObjectClasses();
            for (final String supportedClass : supportedClasses)
            {
                hasSupportedClass = hasSupportedClass && objectClass.contains(
                    supportedClass);
            }

            if (!hasSupportedClass)
            {
                return false; // we're not loading anything, cause nothing is supported
            }

            for (final Field field : annotatedClass.getDeclaredFields())
            {   // BEGIN field iteration
                if (field.isAnnotationPresent(Manager.class))
                {
                    processManager(annotatedClass, field);
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

    private void processManager(final Class annotatedClass, final Field field)
        throws IllegalAccessException
    {
        field.setAccessible(true);
        field.set(object, manager);
        field.setAccessible(false);
    }

    /**
     * Processing for {@link LdapAttribute } annotation where name is '*'
     *
     * @param field the field the annotation is on
     *
     * @throws IllegalAccessException obvious
     */
    private void processLdapAttributes(final Field field)
        throws IllegalAccessException
    {
        field.setAccessible(true);
        field.set(object, attributes);
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
    private void processDN(final Class annotatedClass, final Field field)
        throws IllegalAccessException, NoSuchMethodException
    {
        final DN dnAnnotation = field.getAnnotation(DN.class);
        final String methodName;
        final String fieldName;
        final String firstChar;
        final String endOfString;
        final Method dnGetMethod;
        field.setAccessible(true);
        field.set(object, dn.clone());
        field.setAccessible(false);

        fieldName = field.getName();
        firstChar = fieldName.substring(0, 1);
        endOfString = fieldName.length() > 1 ? fieldName.substring(1) : "";
        methodName = "get" + firstChar.toUpperCase() + endOfString;
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
     * Processing for {@link LdapAttribute } annotation.  If the LdapEntity
     * annotated Class is an instanceof {@link TypeHandler}, the {@link
     * TypeHandler#handleType(Class, List, Class)} will be called for all
     * aggregate fields, instead of the normal type processing that goes on,
     * described by {@link LdapAttribute}
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
    private void processLdapAttribute(final Class annotatedClass,
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
        final String attrName = attrAnnotation.name();
        final String referenceDNMethod = attrAnnotation.referencedDNMethod();
        final Class<?> aggClass = attrAnnotation.aggregateClass();
        final Attribute attr = attributes.get(attrName);
        final NamingEnumeration attrValues =
            attr != null ? attr.getAll() : null;
        final Class refType = field.getType();

        try
        {
            final boolean isAggregate = !Object.class.equals(aggClass);
            if (!isAggregate)
            {   // regular string attributes in a collection, or by themselves.
                if (!String.class.equals(refType) &&
                    attr != null)
                {   // accepts list of ALL attribute values
                    if (List.class.equals(refType))
                    {
                        fieldValue = Collections.list(attrValues);
                    }
                    else if (SortedSet.class.equals(refType))
                    {
                        // This is inefficient, but we don't care,
                        // because the chance of an LDAP entry having
                        // hundreds of values in a single attribute is
                        // SLIM to NONE.
                        fieldValue = new TreeSet(Collections.list(attrValues));
                    }
                }
                else if (attr != null)
                {   // accepts single valued String attributes attribute
                    fieldValue = attr.get();
                }
            }
            else
            {   // BEGIN aggregate processing
                fieldValue = aggClass.newInstance();
                // request to inject an LdapEntity from another ldap entry
                // or use the existing ldap entry to grab Auxiliary attributes

                // local aggregates are loaded from the current ldap entry
                final boolean isLocalAggregate = "".equals(referenceDNMethod);
                if (isLocalAggregate)
                {   // use current ldap entry for population of aggregate
                    final AnnotationProcessor annotationProcessor =
                        new AnnotationProcessor();
                    final LdapEntityHandler handler;
                    handler = new LdapEntityHandler(fieldValue, attributes,
                        dn);
                    handler.setManager(manager);
                    annotationProcessor.addHandler(handler);
                    annotationProcessor.processAnnotations();
                }
                else
                {   // BEGIN foreign ldap entry processing for aggregate
                    final Method dnReferenceMethod = annotatedClass.getMethod(
                        referenceDNMethod);
                    final String dnReference =
                        (String) dnReferenceMethod.invoke(object);
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
                        return;
                    }
                    if (refType.equals(aggClass))
                    {   // field not a collection of any kind, but is a
                        // single object type of the aggClass.
                        fieldValue = getReferencedEntity(aggClass, dnReference,
                            attr.get());
                    }
                    else
                    {   // BEGIN handling collection of aggregates.

                        final List ldapEntities = loadAggregates(aggClass,
                            attrValues, dnReference);

                        if (refType.isArray())
                        {   // convert to the array type used in the field
                            final Object refArray = Array.newInstance(
                                refType.getComponentType(),
                                ldapEntities.size());
                            fieldValue = ldapEntities.toArray(
                                (Object[]) refArray);
                        }
                        else if (List.class.equals(refType))
                        {   // simply unordered List
                            fieldValue = ldapEntities;
                        }
                        else if (SortedSet.class.equals(refType))
                        {   // sorted, Comparable on objects required
                            fieldValue = new TreeSet(ldapEntities);
                        }
                        else if (object instanceof TypeHandler)
                        {
                            fieldValue = ((TypeHandler) object).handleType(
                                field.getType(),
                                ldapEntities, refType);
                        }
                        else
                        {
                            throw new LpaAnnotationException(
                                "unhandled field type: " + refType);
                        }
                    }   // END handling collection of aggregates.
                }   // END foreign ldap entry processing for aggregate
            }   // END aggregate processing

            if (fieldValue != null)
            {   // never set to null, as the contructor may have initialized
                // a default empty collection or something.
                field.set(object, fieldValue);
            }
        }
        finally
        {   // reset java language checks
            field.setAccessible(false);
        }
    }   // END processLdapAttribute()

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
    private Object getReferencedEntity(final Class entityClass,
        final String dnReference, final Object attributeValue)
        throws InvalidNameException
    {
        final String dnLocalReference = dnReference.replace("?",
            Rdn.escapeValue(attributeValue));
        final LdapName ldapName = new LdapName(dnLocalReference);
        return manager.find(entityClass, ldapName);
    }

    @Override
    public Object getObject()
    {
        return object;
    }

    @Override
    public Class<? extends Annotation> getAnnotationClass()
    {
        return LdapEntity.class;
    }

    @Override
    public void noAnnotation(final Class annotatedClass)
    {
        if (annotatedClass == object.getClass())
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
                object.getClass().getName() +
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
