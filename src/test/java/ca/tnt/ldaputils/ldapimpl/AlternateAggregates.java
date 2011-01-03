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
package ca.tnt.ldaputils.ldapimpl;

import ca.tnt.ldaputils.annotations.LdapAttribute;
import ca.tnt.ldaputils.annotations.LdapEntity;
import ca.tnt.ldaputils.impl.LdapEntry;
import ca.tnt.ldaputils.impl.LdapGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Class for testing alternate methods of aggregation that are not used in the
 * primary ldap entry classes provided by this library.
 * <p/>
 * Created :  21/12/10 11:18 PM MST
 *
 * @author Trenton D. Adams
 */
@SuppressWarnings({
    "PublicMethodNotExposedInInterface", "JavaDoc",
    "ReturnOfCollectionOrArrayField", "ZeroLengthArrayAllocation"})
@LdapEntity
public class AlternateAggregates extends LdapEntry
{
    @LdapAttribute(name = "businessCategory", aggregateClass = LdapGroup.class,
        referencedDNMethod = "getCategoryDN"
    )
    private final SortedSet<LdapGroup> sortedSetGroups;

    @LdapAttribute(name = "businessCategory", aggregateClass = LdapGroup.class,
        referencedDNMethod = "getCategoryDN"
    )
    private LdapGroup singleGroup;

    @LdapAttribute(name = "businessCategory", aggregateClass = LdapGroup.class,
        referencedDNMethod = "getCategoryDN"
    )
    private final LdapGroup[] arrayOfGroups;

    @LdapAttribute(name = "businessCategory", aggregateClass = LdapGroup.class,
        referencedDNMethod = "getCategoryDN"
    )
    private final List<LdapGroup> listOfGroups;

    public AlternateAggregates()
    {
        sortedSetGroups = new TreeSet<LdapGroup>();
        arrayOfGroups = new LdapGroup[0];
        listOfGroups = new ArrayList<LdapGroup>();
    }

    /**
     * returns the category dn with a bind variable.
     *
     * @return the dn with a bind variable.
     */
    @SuppressWarnings(
        {"PublicMethodNotExposedInInterface", "MethodMayBeStatic"})
    public String getCategoryDN()
    {
        return "cn=?,ou=bus-categories,dc=example,dc=com";
    }

    public SortedSet<LdapGroup> getSortedSetGroups()
    {
        return sortedSetGroups;
    }

    public LdapGroup getSingleGroup()
    {
        return singleGroup;
    }

    public LdapGroup[] getArrayOfGroups()
    {
        return arrayOfGroups;
    }

    public List<LdapGroup> getListOfGroups()
    {
        return listOfGroups;
    }
}
