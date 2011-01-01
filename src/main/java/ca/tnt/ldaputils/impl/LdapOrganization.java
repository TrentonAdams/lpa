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
package ca.tnt.ldaputils.impl;

import ca.tnt.ldaputils.ILdapGroup;
import ca.tnt.ldaputils.ILdapOrganization;
import ca.tnt.ldaputils.annotations.LdapAttribute;
import ca.tnt.ldaputils.annotations.LdapEntity;
import ca.tnt.ldaputils.annotations.TypeHandler;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Implements an LDAP organization object.
 * <p/>
 * Created :  16-Apr-2006 10:25:36 PM MST
 * <p/>
 * Modified : $Date$ UTC
 * <p/>
 * Revision : $Revision$
 * <p/>
 * TODO: Make getBusinessCategories() return a list of all businessCategories
 * for display on the web.
 *
 * @author Trenton D. Adams <trent.nospam@telus.net>
 */
@LdapEntity(requiredObjectClasses = {"organization"})
public class LdapOrganization extends LdapEntry
    implements ILdapOrganization, Comparable, TypeHandler
{
    private static final Logger logger = Logger.getLogger(
        LdapOrganization.class);

    @LdapAttribute(name = "businessCategory", aggregateClass = LdapGroup.class,
        referencedDNMethod = "getCategoryDN"
        /*"cn=?,ou=bus-categories,dc=example,dc=com"*/
    )
    private SortedMap<String, LdapGroup> businessCategories;

    @LdapAttribute(name = "telephoneNumber")
    private String telephoneNumber;
    @LdapAttribute(name = "facsimileTelephoneNumber")
    private String facsimileTelephoneNumber;
    @LdapAttribute(name = "street")
    private String street;
    @LdapAttribute(name = "postOfficeBox")
    private String postOfficeBox;
    @LdapAttribute(name = "postalAddress")
    private String postalAddress;
    @LdapAttribute(name = "postalCode")
    private String postalCode;
    @LdapAttribute(name = "l")
    private String locality;
    @LdapAttribute(name = "o")
    private String organization;

    /**
     * Initializes an empty SortedMap for the businessCategories.
     */
    public LdapOrganization()
    {
        businessCategories = new TreeMap<String, LdapGroup>();
    }

    public SortedMap<String, ? extends ILdapGroup> getBusinessCategories()
    {
        logger.debug("categories: " + businessCategories);
        return businessCategories == null ? new TreeMap<String, LdapGroup>() :
            Collections.unmodifiableSortedMap(businessCategories);
    }

    public String getTelephoneNumber()
    {
        return telephoneNumber;
    }

    public String getFacsimileTelephoneNumber()
    {
        return facsimileTelephoneNumber;
    }

    public String getStreet()
    {
        return street;
    }

    public String getPostOfficeBox()
    {
        return postOfficeBox;
    }

    public String getPostalCode()
    {
        return postalCode;
    }

    public String getPostalAddress()
    {
        return postalAddress;
    }

    public String getLocality()
    {
        return locality;
    }

    public String getOrganization()
    {
        return organization;
    }

    public void setBusinessCategories(final String[] categories,
        final int operation)
    {
        for (final String category : categories)
        {
            modifyBatchAttribute(REPLACE_ATTRIBUTE,
                "businessCategory", category);
        }
    }

    public void setTelephoneNumber(final String telephoneNumber,
        final int operation)
    {
        modifyBatchAttribute(operation, "telephoneNumber", telephoneNumber);
        this.telephoneNumber = telephoneNumber;
    }

    public void setFacsimileTelephoneNumber(final String fax,
        final int operation)
    {
        modifyBatchAttribute(operation, "facsimileTelephoneNumber", fax);
        facsimileTelephoneNumber = fax;
    }

    public void setStreet(final String street, final int operation)
    {
        modifyBatchAttribute(operation, "street", street);
        this.street = street;
    }

    public void setPostOfficeBox(final String postOfficeBox,
        final int operation)
    {
        modifyBatchAttribute(operation, "postOfficeBox", postOfficeBox);
        this.postOfficeBox = postOfficeBox;
    }

    public void setPostalCode(final String postalCode, final int operation)
    {
        modifyBatchAttribute(operation, "postalCode", postalCode);
        this.postalCode = postalCode;
    }

    public void setPostalAddress(final String postalAddress,
        final int operation)
    {
        modifyBatchAttribute(operation, "postalAddress", postalAddress);
        this.postalAddress = postalAddress;
    }

    public void setLocality(final String city, final int operation)
    {
        modifyBatchAttribute(operation, "l", city);
        locality = city;
    }

    public void setOrganization(final String organizationName,
        final int operation)
    {
        modifyBatchAttribute(operation, "o", organizationName);
        organization = organizationName;
    }

    @Override
    public String toString()
    {
        return super.toString() + ", LdapOrganization{" +
            "businessCategories=" + businessCategories +
            ", telephoneNumber='" + telephoneNumber + '\'' +
            ", facsimileTelephoneNumber='" + facsimileTelephoneNumber + '\'' +
            ", street='" + street + '\'' +
            ", postOfficeBox='" + postOfficeBox + '\'' +
            ", postalAddress='" + postalAddress + '\'' +
            ", postalCode='" + postalCode + '\'' +
            ", locality='" + locality + '\'' +
            ", organization='" + organization + '\'' +
            '}';
    }

    /**
     * returns the category dn with a bind variable.
     * <p/>
     * CRITICAL replace with a configurable property.
     *
     * @return the dn with a bind variable.
     */
    @SuppressWarnings({"PublicMethodNotExposedInInterface"})
    public String getCategoryDN()
    {
        return "cn=?,ou=bus-categories,dc=example,dc=com";
    }

    @SuppressWarnings({"ChainedMethodCall", "NonFinalFieldReferenceInEquals"})
    @Override
    public boolean equals(final Object o)
    {
        final LdapOrganization rhs = (LdapOrganization) o;

        return new EqualsBuilder()
            .appendSuper(super.equals(o))
            .append(businessCategories, rhs.businessCategories)
            .append(getBusinessCategories(), rhs.getBusinessCategories())
            .append(telephoneNumber, rhs.telephoneNumber)
            .append(facsimileTelephoneNumber, rhs.facsimileTelephoneNumber)
            .append(street, rhs.street)
            .append(postOfficeBox, rhs.postOfficeBox)
            .append(postalAddress, rhs.postalAddress)
            .append(postalCode, rhs.postalCode)
            .append(locality, rhs.locality)
            .append(organization, rhs.organization)
            .isEquals();
    }

    @SuppressWarnings(
        {"ChainedMethodCall", "NonFinalFieldReferencedInHashCode"})
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37).
            appendSuper(super.hashCode()).
            append(businessCategories).
            append(getBusinessCategories()).
            append(telephoneNumber).
            append(facsimileTelephoneNumber).
            append(street).
            append(postOfficeBox).
            append(postalAddress).
            append(postalCode).
            append(locality).
            append(organization).
            toHashCode();
    }

    @SuppressWarnings({"ChainedMethodCall", "CompareToUsesNonFinalVariable"})
    @Override
    public int compareTo(final Object o)
    {
        final LdapOrganization myClass = (LdapOrganization) o;
        return new CompareToBuilder()
            .appendSuper(super.compareTo(o))
            .append(businessCategories, myClass.businessCategories)
            .append(getBusinessCategories(), myClass.getBusinessCategories())
            .append(telephoneNumber, myClass.telephoneNumber)
            .append(facsimileTelephoneNumber, myClass.facsimileTelephoneNumber)
            .append(street, myClass.street)
            .append(postOfficeBox, myClass.postOfficeBox)
            .append(postalAddress, myClass.postalAddress)
            .append(postalCode, myClass.postalCode)
            .append(locality, myClass.locality)
            .append(organization, myClass.organization)
            .toComparison();
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public Object handleType(final Class classType, final List list,
        final Class refType)
    {
        Object fieldValue = null;
        if (SortedMap.class.equals(refType))
        {   // we know what to do
            final Map categoryMap = new TreeMap();
            for (final Object ldapGroup : list)
            {
                categoryMap.put(((ILdapGroup) ldapGroup).getCN(),
                    ldapGroup);
            }
            fieldValue = categoryMap;
        }

        return fieldValue;
    }
}
