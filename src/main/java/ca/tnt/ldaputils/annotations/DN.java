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

import ca.tnt.ldaputils.LdapManager;

import javax.naming.ldap.LdapName;
import java.lang.annotation.*;

/**
 * A field annotated with this is indicating that it is an LdapName variable for
 * storing the fully qualified distinguished name.  This is a special case for
 * an LDAP attribute, as the DN is REQUIRED for storage and what not.  So, a
 * field and a method must be defined.  Usually the DN that was used to find the
 * object, is injected here.  The alternative is the DN that was found during a
 * {@link LdapManager#search(LdapName, String, String, String[])} operation.
 *
 * The method name MUST be of the form "getPropetyX", where the first letter
 * of the field name is capitalized after "get".  So, in this case, the
 * member field is "propertyX".
 * <p/>
 * Created :  16-Aug-2010 8:06:13 PM MST
 *
 * @author Trenton D. Adams
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DN
{
}
