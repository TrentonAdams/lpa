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
/*
 * LDAPGroup.java
 *
 * Created on April 14, 2006, 6:04 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ca.tnt.ldaputils;

import javax.naming.InvalidNameException;
import java.util.Map;
import java.util.SortedSet;

/**
 * @author trenta
 */
public interface ILdapGroup extends ILdapEntry
{
    /**
     * TODO : Implement a getMembers(member type)
     * <p/>
     * TODO : Change the default return type to LDAP_GROUP
     *
     * @param objectType
     *
     * @return map of members with organization as the key.  The members are of
     *         type @link{LDAPFactory.LDAP_TNT_BUSINESS} @param keyAttribute
     */
    Map getMembers(String keyAttribute, int objectType)
        throws InvalidNameException;

    /**
     * Add a member to this group.
     *
     * @param ldapEntry the ldap object corresponding to the member to add.
     */
    void addMember(ILdapEntry ldapEntry);

    /**
     * Remove a member from this group.
     *
     * @param ldapEntry the ldap object corresponding to the member to add.
     */
    void removeMember(ILdapEntry ldapEntry);

    /**
     * Returns a non-null SortedSet of group members.  If there are no members,
     * the set's size is ZERO.
     *
     * @return all members in DN format, in alphabetical order.
     */
    SortedSet getMembers();
}
