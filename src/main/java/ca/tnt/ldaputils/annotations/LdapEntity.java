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

import ca.tnt.ldaputils.impl.LdapEntry;

import java.lang.annotation.*;

/**
 * If you annotate with this, your POJO MUST contain an @DN annotation as well.
 * <p/>
 * It is recommended, though not required, that you inherit from {@link
 * LdapEntry}, as it covers a lot of functionality that is useful for updating
 * LDAP.  If you do not plan on doing updates to LDAP, then there is no added
 * benefit when inheriting from LdapEntry, and a simple POJO will suit your
 * needs just fine.
 * <p/>
 * Created :  16-Aug-2010 7:26:38 PM MST
 *
 * @author Trenton D. Adams
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LdapEntity
{
    /**
     * The object classes that must be present for loading of this object to be
     * successful.
     * <p/>
     * e.g. organization, labeledURIObject, person, inetOrgPerson,
     * organizationalPerson, etc
     *
     * @return the String array of objectClasses
     */
    String[] requiredObjectClasses() default {};
}
