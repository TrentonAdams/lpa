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

import ca.tnt.ldaputils.ILdapGroup;
import ca.tnt.ldaputils.ILdapOrganization;
import ca.tnt.ldaputils.annotations.LdapAttribute;
import ca.tnt.ldaputils.annotations.LdapEntity;
import ca.tnt.ldaputils.impl.LdapEntry;
import ca.tnt.ldaputils.impl.LdapOrganization;

import java.util.SortedMap;

/**
 * Just an example of aggregation; this is not actually useful, since
 * LdapOrganization actually extends LdapEntry, making this completely useless
 * in real life.
 * <p/>
 * Created :  24-Aug-2010 1:22:05 AM MST
 *
 * @author Trenton D. Adams
 */
@LdapEntity
public class LdapAggregation extends LdapEntry implements ILdapOrganization
{
    @LdapAttribute(name = "", aggregateClass = LdapOrganization.class)
    protected LdapOrganization ldapOrg;

    @Override
    public SortedMap<String, ? extends ILdapGroup> getBusinessCategories()
    {
        return ldapOrg.getBusinessCategories();
    }

    @Override
    public String getTelephoneNumber()
    {
        return ldapOrg.getTelephoneNumber();
    }

    @Override
    public String getFacsimileTelephoneNumber()
    {
        return ldapOrg.getFacsimileTelephoneNumber();
    }

    @Override
    public String getStreet()
    {
        return ldapOrg.getStreet();
    }

    @Override
    public String getPostOfficeBox()
    {
        return ldapOrg.getPostOfficeBox();
    }

    @Override
    public String getPostalCode()
    {
        return ldapOrg.getPostalCode();
    }

    @Override
    public String getPostalAddress()
    {
        return ldapOrg.getPostalAddress();
    }

    @Override
    public String getLocality()
    {
        return ldapOrg.getLocality();
    }

    @Override
    public String getOrganization()
    {
        return ldapOrg.getOrganization();
    }

    @Override
    public void setBusinessCategories(final String[] categories,
        final int operation)
    {
        ldapOrg.setBusinessCategories(categories, operation);
    }

    @Override
    public void setTelephoneNumber(final String telephoneNumber,
        final int operation)
    {
        ldapOrg.setTelephoneNumber(telephoneNumber, operation);
    }

    @Override
    public void setFacsimileTelephoneNumber(final String fax,
        final int operation)
    {
        ldapOrg.setFacsimileTelephoneNumber(fax, operation);
    }

    @Override
    public void setStreet(final String street, final int operation)
    {
        ldapOrg.setStreet(street, operation);
    }

    @Override
    public void setPostOfficeBox(final String postOfficeBox,
        final int operation)
    {
        ldapOrg.setPostOfficeBox(postOfficeBox, operation);
    }

    @Override
    public void setPostalCode(final String postalCode, final int operation)
    {
        ldapOrg.setPostalCode(postalCode, operation);
    }

    @Override
    public void setPostalAddress(final String postalAddress,
        final int operation)
    {
        ldapOrg.setPostalAddress(postalAddress, operation);
    }

    @Override
    public void setLocality(final String city, final int operation)
    {
        ldapOrg.setLocality(city, operation);
    }

    @Override
    public void setOrganization(final String organizationName,
        final int operation)
    {
        ldapOrg.setOrganization(organizationName, operation);
    }

}
