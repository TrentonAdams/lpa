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
package ca.tnt.ldaputils.ldapimpl;

import ca.tnt.ldaputils.annotations.DN;
import ca.tnt.ldaputils.annotations.LdapEntity;

import javax.naming.ldap.LdapName;

/**
 * Created by IntelliJ IDEA.
 * <p/>
 * Created :  23-Aug-2010 1:26:35 AM MST
 *
 * @author Trenton D. Adams
 */
@LdapEntity
public class LdapEntityBadDNMethod
{
    @DN
    protected LdapName dn;

    public void getDn()
    {

    }
}
