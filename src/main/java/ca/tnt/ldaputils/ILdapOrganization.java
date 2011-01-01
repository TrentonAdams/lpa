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
package ca.tnt.ldaputils;

import ca.tnt.ldaputils.impl.LdapGroup;

import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * LDAPOrganization represents an object with a class of "organization"
 * <p/>
 * Created :  15-Apr-2006 12:48:52 PM MST
 * <p/>
 * Modified : $Date$ UTC
 * <p/>
 * Revision : $Revision$
 * <p/>
 * @author Trenton D. Adams <trenta@athabascau.ca>
 */
public interface ILdapOrganization extends ILdapEntry
{
    /**
     * Retrieves a Map of categories where the key is the category name, and the
     * value is the ILdapGroup.  This allows iteration through the keys, just
     * for the category names, or iteration through the values for the values,
     * or retrieving values by key
     *
     * @return the Map of categories as described
     */
    public SortedMap<String, ? extends ILdapGroup> getBusinessCategories();

    public String getTelephoneNumber();

    public String getFacsimileTelephoneNumber();

    public String getStreet();

    public String getPostOfficeBox();

    public String getPostalCode();

    public String getPostalAddress();

    public String getLocality();

    public String getOrganization();

    public void setBusinessCategories(String[] categories, int operation);

    public void setTelephoneNumber(String telephoneNumber, int operation);

    public void setFacsimileTelephoneNumber(String fax, int operation);

    public void setStreet(String street, int operation);

    public void setPostOfficeBox(String postOfficeBox, int operation);

    public void setPostalCode(String postalCode, int operation);

    public void setPostalAddress(String postalAddress, int operation);

    public void setLocality(String city, int operation);

    public void setOrganization(String organizationName, int operation);

}
