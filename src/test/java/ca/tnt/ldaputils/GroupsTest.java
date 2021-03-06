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
package ca.tnt.ldaputils;

import ca.tnt.ldaputils.impl.LdapGroup;
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

/**
 * Created by IntelliJ IDEA.
 * <p/>
 * Created :  01/01/11 2:03 PM MST
 *
 * @author Trenton D. Adams
 */
@SuppressWarnings({"JavaDoc", "ClassWithoutConstructor", "ChainedMethodCall"})
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
                        "o: example\n" +
                        "dc: example"))
        })
@ApplyLdifFiles({
    "example.schema.ldif",
    "add-domain.ldif",
    "add-busgroups.ldif",
    "add-businesses.ldif"})
public class GroupsTest extends AbstractLdapTestUnit
{
    private LdapManager manager;

    @Before
    public void setUp()
    {
        manager = new LdapManager("localhost", "" + ldapServer.getPort(),
            "uid=admin,ou=system", "secret");
    }

    @SuppressWarnings({"ChainedMethodCall"})
    @Test
    public void testLdapGroup() throws InvalidNameException
    {
        final LdapName ldapName = new LdapName(
            "cn=Hair Salons,ou=bus-categories,dc=example,dc=com");
        try
        {
            final LdapGroup ldapEntry = (LdapGroup) manager.find(
                LdapGroup.class, ldapName);
            Assert.assertEquals("number of members", 4,
                ldapEntry.getMembers().size());
        }
        catch (IllegalArgumentException e)
        {   // this should happen, and we need to make a test case for this.
            e.printStackTrace();
            Assert.fail("ldap group should have been loaded");
        }
    }

    @SuppressWarnings({"ChainedMethodCall"})
    @Test
    public void testCategoryGroup() throws InvalidNameException
    {
        final LdapName ldapName = new LdapName(
            "cn=categories,ou=bus-categories,dc=example,dc=com");
        try
        {
            final LdapGroup ldapEntry = (LdapGroup) manager.find(
                LdapGroup.class, ldapName);
            Assert.assertEquals("number of categories", 4,
                ldapEntry.getBusinessCategories().size());
        }
        catch (IllegalArgumentException e)
        {   // this should happen, and we need to make a test case for this.
            e.printStackTrace();
            Assert.fail("ldap group should have been loaded");
        }
    }
}
