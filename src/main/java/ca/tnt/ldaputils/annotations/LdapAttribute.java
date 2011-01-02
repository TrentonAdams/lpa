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
package ca.tnt.ldaputils.annotations;

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
 * entire entry.  As such, the dn is determined during a "search" and punched in
 * as an LdapAttribute, manually by the factory, or cloned from the LdapName dn
 * given by the developer (when doing a direct entry retrieval, as opposed to a
 * search).
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
 * have an email address, but perhaps you want that functionality, among other
 * things, so you add more {@link LdapEntity} annotated object classes to that
 * {@link LdapEntity} annotated object class, to allow it to have an email
 * address.
 * <p/>
 * Best practice, is to define interfaces, that all participating classes
 * implement objects implement.  Using the previous example, we might define
 * {@link ILdapOrganization} and {@link ILabeledURI}. Then, we would aggregate
 * the two implementations of those interfaces, into our own object, while also
 * implementing the interfaces.  After that, we'd proxy each method call to the
 * actual implementation object.  There is an example of this concept, in the
 * unit tests for LPA.  If you need this functionality, we recommend that you
 * take a look at the classes mentioned, and the LdapAggregation class in the
 * unit test source directory.
 * <p/>
 * Another feature of aggregation, is the ability to load an entirely different
 * LDAP entry, to inject into the field.  This is done for things like groups,
 * where they have a series of unique member attributes.  Or, in some cases,
 * other entries refer to the group, via some attribute.  So, we also prevent
 * recursive loading, so that each object can refer to the other.  e.g. the
 * group can refer to it's members, while all those members may refer to their
 * groups.
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
 * CRITICAL add support for other "types" of attributes.  For example, LdapName
 * is a special case, for attributes that are DN qualified.  It may be that we
 * can add another annotation called LdapType, to define the "type" as being
 * able to handle conversion from "attribute" format, to a specific type.  For
 * example, it would be nice if uniqueMember attributes of a group would come
 * into your object as an LdapName, as opposed to a "string".  So, we might go
 * at-sign LdapAttribute(name = "uniqueMember", array = true, type = LdapName,
 * typeHandler = LdapNameHandler)  Also determine if annotations can work on
 * static members, because it may be that we want to have a utility class that
 * handles all sorts of different types.  We can implement the above, using
 * another annotation for a method that can handle the storage, and return the
 * exact Object desired.
 * <p/>
 * Created :  16-Aug-2010 10:43:42 PM MST
 * <p/>
 * Modified : $Date$ UTC
 * <p/>
 * Revision : $Revision$
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
     * but instead, the values will be stored in a String (only one value), or
     * collection (multiple valued attributes) of Strings, depending on the
     * field's type definition.  Note that only List, SortedSet, String, and a
     * java native array are supported for the field type definition.
     * <p/>
     * There is no need for any of these types to be pre-initialized, as they
     * will be replaced.
     * <p/>
     * It is implied that when using a SortedSet, the LdapEntity annotated class
     * MUST also implement the {@link Comparable} interface.  If it does not
     * implement this interface, then the SortedSet will throw a
     * ClassCastException, because it does not implement the Comparable
     * interface.
     * <p/>
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
     * The method in the parent object of the field, which returns the reference
     * DN of the entry to load.  It must have a bind variable ('?') in it to
     * designate the location to replace with the value of the ldap attribute
     * defined by {@link #name()}. An LdapName will be constructed from this DN,
     * and used as the location for the ldap aggregate we're loading.  If a '?'
     * is not present, an LdapNamingException will be thrown indicating as
     * much.
     * <p/>
     * For example, you may have a DN of "cn=?,ou=bus-categories,dc=example,dc=com",
     * where '?' is replaced by the value of the attribute.  Perhaps you have a
     * "businessCategory" attribute with "Hair Salons".  The final DN lookup for
     * the aggregation would be "cn=Hair Salons,ou=bus-categories,..."
     * <p/>
     * CRITICAL somehow take into account infinite recursion.  e.g. we may have
     * support for loading groups, from a certain attribute in an entry, like
     * the above.  Those groups however, may also contain some sort of
     * annotations, that try to load the objects in their member attributes.
     * This could cause an infinite recursive loop.  We need to find a way of
     * detecting this, and referencing each other.
     * <p/>
     * CRITICAL as per the above, we should look into how JPA does it, as I've
     * forgotten.  They use annotations to make it simpler I think, so that no
     * complex logic is required.  What they do is have a "mappedBy" member in
     * the annotation, which is the name of a field containing the reference
     * back the existing entry.
     *
     * @return the method that returns the DN entry to load, with the bind
     *         variable ('?')
     */
    String referencedDNMethod() default "";
}
