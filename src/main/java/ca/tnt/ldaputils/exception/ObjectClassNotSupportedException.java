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
package ca.tnt.ldaputils.exception;

/**
 * Created by IntelliJ IDEA.
 * <p/>
 * Created :  14-Apr-2006 8:17:54 PM MST
 *
 * @author trenta
 */
public class ObjectClassNotSupportedException extends LdapNamingException
{
    public ObjectClassNotSupportedException(final String message)
    {
        super(message);
    }
}
