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

/**
 * Defines the types of LDAP objectClass(es).  There are essentially two types:
 * {@link #STRUCTURAL}, and {@link #AUXILIARY}.
 * <p/>
 * Created :  10-Dec-2010 4:00:38 PM MST
 *
 * @author Trenton D. Adams
 */
public enum ObjectClassType
{
    /**
     * An Auxiliary ldap objectclass is just an add-on to the entry.  For
     * example, you might define an ldap entry as having the objectClass of
     * "labeledURIObject", which simply means that entry can now accept web site
     * urls with a space separated description.
     */
    AUXILIARY,

    /**
     * a structural ldap objectClass is one that provides the base attribute
     * definitions for an ldap entry.  It can be supplemented by an {@link
     * #AUXILIARY} objectClass, as defined in {@link #AUXILIARY}.  Or, it can be
     * a hierarchy of structural objectClass(es).  A good example of this type
     * of objectClass is the "person" objectClass.  A good example of
     * hierarchical structural objectClasses are the inetOrgPerson ->
     * organizationalPerson -> person object classes, where person is the base
     * most class.
     */
    STRUCTURAL
}
