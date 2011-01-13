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
package ca.tnt.ldaputils.annotations;

import ca.tnt.ldaputils.ILabeledURI;
import ca.tnt.ldaputils.ILdapOrganization;

import javax.naming.directory.Attributes;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares the member field that stores an attribute of a particular name.
 * <p/>
 * The "dn" attribute is a special case, seeing "dn" is not loaded by the LDAP
 * API, as it is not technically an attribute, but is the namespace of the
 * entire entry.  As such, the dn is determined during a "find" and punched in
 * as an LdapAttribute, manually by the factory, or cloned from the LdapName dn
 * given by the developer (when doing a direct entry retrieval, as opposed to a
 * search).  See {@link DN} for more information.
 * <p/>
 * If you are using a "Collection", you need to initialize it in your no args
 * constructor, otherwise a NullPointerException will be thrown by the system,
 * with a message indicating as much.
 * <p/>
 * Because of the nature of LDAP, being able to have multiple objectClass
 * attribute values on any given ldap entry, thereby defining it as using
 * attributes from multiple schema types, we need a way of doing multiple
 * inheritance, so as to replicate the features of LDAP.  Namely, it would be
 * nice, in some circumstances, to say that an object implementation is both an
 * organization, and a labeledURIObject, where they both have different
 * implementation objects annotated with {@link LdapEntity}. We do this via
 * object aggregation.
 * <p/>
 * Basically, the developer may have one Class that implements most of the
 * functionality of a particular type of ldap entry, and another that has added
 * functionality they want as well.  For example, an ldap organization cannot
 * have a labeled URI attribute in ldap, unless it has an objectClass of
 * "labeledURIObject", but perhaps you want that functionality, among other
 * things, so you add a labeled URI {@link LdapEntity} annotated object class as
 * a field inside the ldap organization {@link LdapEntity} annotated object
 * class, to allow it to have a url address.  In the ldap world, we do this via
 * the "objectClass" attribute, which can have multiple types of attributes.  As
 * an example, your objectClass attributes in ldap, may now look like
 * <p/>
 * <pre>
 * objectClass: labeledURIObject
 * objectClass: organization
 * objectClass: top
 * </pre>
 * <p/>
 * Best practice, is to define interfaces, that all participating classes
 * implement.  Using the previous example, we might define {@link
 * ILdapOrganization} and {@link ILabeledURI}. Then, we would aggregate the two
 * implementations of those interfaces, into our own object, while also
 * implementing the interfaces.  After that, we'd proxy each method call to the
 * actual implementation object.  There is an example of this concept, in the
 * unit tests for LPA.  If you need this functionality, we recommend that you
 * take a look at the classes mentioned, and the LdapAggregation class in the
 * unit test source directory.
 * <p/>
 * Another feature of aggregation, is the ability to load an entirely different
 * LDAP entry, to inject into the field.  This is done for things like groups,
 * where they have a series of unique member attributes.  Or, in some cases,
 * other entries refer to the group, via some attribute.  So, we also plan on
 * preventing recursive loading, so that each object can refer to the other.
 * However, recursive loading is not supported where ldap entries refer to each
 * other. e.g. the group can refer to it's members, while all those members may
 * refer to their groups.
 * <p/>
 * FEATURE we need to think about the implications of having entries in multiple
 * groups, and those groups pointing to multiple other entries.  This could
 * potentially cause a very serious memory collection issue, as they all hold
 * references to each other.  Perhaps we need to include a feature to prevent
 * this from occurring, by, rather than referencing each other, they get clones.
 * But, we also need to allow direct references, as it can save memory, and is
 * more efficient in some circumstances.  One good example, is when you actually
 * do want to load a whole bunch of groups and members of those groups, and
 * cache them. The memory footprint will be much smaller when groups use
 * references to their members, and vice-versa, rather than a clone.
 * <p/>
 * CRITICAL ldap types (issue-16)
 * <p/>
 * CRITICAL annotation documentation restructuring (issue-6) Created :
 * 16-Aug-2010 10:43:42 PM MST
 *
 * @author Trenton D. Adams
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface LdapAttribute
{
    /**
     * The name of the attribute to store in this field, or '*' to store all
     * attributes in this field.  A name of '*' requests storage of ALL
     * attributes in an {@link Attributes} field variable. Fields with a name of
     * '*' are ignored during binding, updating, and are used only for queries.
     * <p/>
     * In the case of an aggregate, where we take another LDAP entry, and inject
     * it, the name of the attribute in the current ldap entry that should have
     * it's value injected into the DN returned by {@link #referencedDNMethod}.
     *
     * @return the name of the attribute
     */
    String name();

    /**
     * The {@link LdapEntity} annotated class that should be used for the
     * aggregate. The default class of Object.class, implies that this ldap
     * attribute will not be stored in an aggregate {@link LdapEntity} object,
     * but instead, the values will be stored in a String (only one value), a
     * byte array (a binary object, only one), or a collection (multiple valued
     * attributes of String or byte array), depending on the field's type
     * definition. Note that for Sun's LDAP provider, String, and byte arrays
     * are the only options supported for attribute values.
     * <p/>
     * Note that only List, SortedSet, String, and a java native array are
     * supported for the field type definition.
     * <p/>
     * There is no need for any of these types to be pre-initialized, as they
     * will be replaced if they exist in LDAP.
     * <p/>
     * It is implied that when using a SortedSet, the LdapEntity annotated class
     * MUST also implement the {@link Comparable} interface.  If it does not
     * implement this interface, then the SortedSet will throw a
     * ClassCastException, because it does not implement the Comparable
     * interface.
     *
     * @return the aggregate {@link LdapEntity} annotated class, or Object.class
     *         as a default.
     */
    Class<?> aggregateClass() default Object.class;

    /**
     * Currently unused, may be useful in the future for something.
     * <p/>
     * Defines the objectClass type used when doing aggregation.  It may be
     * {@link ObjectClassType#STRUCTURAL} or {@link ObjectClassType#AUXILIARY}
     * defaults to {@link ObjectClassType#AUXILIARY}, as that is the most likely
     * one to be used when injecting into an existing LDAP object
     *
     * @return the type defined
     */
    ObjectClassType ocType() default ObjectClassType.AUXILIARY;

    /**
     * If {@link #aggregateClass()} is used, this refers to the method in the
     * same object as the field, which returns the reference DN of the entry to
     * load.  Run on sentence, READY?  The string returned must have a bind
     * variable ('?') in it, to designate the location to replace with the value
     * of the LDAP attribute defined by {@link #name()}. An LdapName will be
     * constructed from the resulting DN, and used as the location for the LDAP
     * aggregate we're loading.  If a '?' is not present, an LdapNamingException
     * will be thrown indicating as much.
     * <p/>
     * For example, you may have a DN of "cn=?,ou=bus-categories,dc=example,dc=com",
     * where '?' is replaced by the value of the attribute.  Perhaps you have a
     * "businessCategory" attribute set to "Hair Salons".  The final DN lookup
     * for the aggregation would be "cn=Hair Salons,ou=bus-categories,..."
     * <p/>
     * In addition, an aggregate that has this set to the default value, is
     * implicitly a local aggregate.  See the {@link LdapAttribute LdapAttribute
     * class} documentation for more information.
     * <p/>
     * CRITICAL fix infinite recursion (issue-15)
     *
     * @return the method that returns the DN entry to load, with the bind
     *         variable ('?')
     */
    String referencedDNMethod() default "";

    /**
     * Exact same as {@link #referencedDNMethod()} except that there is no need
     * for a method, this returns the DN with replaceable parameter embedded.
     *
     * TEST test reference DNs properly (issue-25)
     *
     * @return the DN reference with the bind variable ('?')
     */
    String referencedDN() default "";
}
