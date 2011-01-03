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

import ca.tnt.ldaputils.impl.LdapOrganization;
import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.annotations.ContextEntry;
import org.apache.directory.server.core.annotations.CreateDS;
import org.apache.directory.server.core.annotations.CreatePartition;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.server.core.integ.FrameworkRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import java.util.SortedMap;

/**
 * Created by IntelliJ IDEA.
 * <p/>
 * Created :  19/12/10 12:16 AM MST
 *
 * @author Trenton D. Adams
 */
@SuppressWarnings(
    {"JavaDoc", "ClassWithoutConstructor", "PublicMethodNotExposedInInterface"})
@RunWith(FrameworkRunner.class)
@CreateLdapServer(
    transports =
        {
            @CreateTransport(protocol = "LDAP")
        })
@CreateDS(allowAnonAccess = false, name = "example-partition",
    partitions =
        {
            @CreatePartition(
                name = "example",
                suffix = "dc=example,dc=com",
                contextEntry = @ContextEntry(
                    entryLdif = "dn: dc=example,dc=com\n" +
                        "objectclass: dcObject\n" +
                        "objectclass: organization\n" +
                        "o: Example\n" +
                        "dc: example"))
        })
@ApplyLdifFiles({
    "example.schema.ldif",
    "add-domain.ldif",
    "add-busgroups.ldif",
    "add-businesses.ldif"})
public class LdapOrganizationTest extends AbstractLdapTestUnit
{
    private LdapManager manager;

    @Before
    public void setUp()
    {
        manager = new LdapManager("localhost", "" + ldapServer.getPort(),
            "uid=admin,ou=system", "secret");
    }

    @Test
    public void testLdapOrg() throws InvalidNameException
    {
        final LdapName ldapName = new LdapName(
            "o=Pulp Mill.,ou=businesses,dc=example,dc=com");
        final LdapOrganization ldapEntry = (LdapOrganization) manager.find(
            LdapOrganization.class, ldapName);
        final SortedMap<String, ? extends ILdapGroup> categories =
            ldapEntry.getBusinessCategories();
        Assert.assertNotNull("Manufacturing category does not exist",
            categories.get("Manufacturing"));
        Assert.assertNotNull("Pulp & Paper Products category does not exist",
            categories.get("Pulp & Paper Products"));
        Assert.assertEquals("Pulp Mill business categories", 2,
            categories.size());
        Assert.assertEquals("Organization should be Pulp Mill",
            "Pulp Mill.", ldapEntry.getOrganization());
    }

}
