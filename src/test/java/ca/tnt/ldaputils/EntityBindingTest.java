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

import ca.tnt.ldaputils.impl.LdapOrganization;
import ca.tnt.ldaputils.ldapimpl.LdapAggregation;
import junit.framework.Assert;
import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.annotations.ContextEntry;
import org.apache.directory.server.core.annotations.CreateDS;
import org.apache.directory.server.core.annotations.CreatePartition;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.server.core.integ.FrameworkRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;

/**
 * Created by IntelliJ IDEA.
 * <p/>
 * Created :  02/01/11 3:58 PM MST
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
public class EntityBindingTest extends AbstractLdapTestUnit
{
    private LdapManager manager;

    @Before
    public void setUp()
    {
        manager = new LdapManager("localhost", "" + ldapServer.getPort(),
            "uid=admin,ou=system", "secret");
    }

    @Test
    public void testOrganizationBind() throws InvalidNameException
    {
        final LdapName ldapName = new LdapName(
            "o=New Organization,ou=businesses,dc=example,dc=com");

        final long beforeBind;
        final long afterBind;

        beforeBind = System.currentTimeMillis();
        final ILdapOrganization organization = new LdapOrganization();
        organization.setDn(ldapName);
        organization.setOrganization("New Organization", 0);
        organization.setLocality("Some Town", 0);
        organization.setTelephoneNumber("(123) 555-5555", 0);
        organization.setStreet("123 Ldap Way", 0);
        organization.setPostalCode("A1A 2B2", 0);
        manager.bind(organization);
        afterBind = System.currentTimeMillis();
        System.out.println("binding took: " + (afterBind - beforeBind) + "ms");


        final long beforeQuery;
        final long afterQuery;

        beforeQuery = System.currentTimeMillis();
        final ILdapOrganization organization2 =
            (ILdapOrganization) manager.find(LdapOrganization.class, ldapName);
        afterQuery = System.currentTimeMillis();
        System.out.println("query took: " + (afterQuery - beforeQuery) + "ms");

        Assert.assertEquals("dn", organization.getDn(), organization2.getDn());
        Assert.assertEquals("organization", organization.getOrganization(),
            organization2.getOrganization());
        Assert.assertEquals("locality", organization.getLocality(),
            organization2.getLocality());
        Assert.assertEquals("telephone", organization.getTelephoneNumber(),
            organization2.getTelephoneNumber());
        Assert.assertEquals("street", organization.getStreet(),
            organization2.getStreet());
        Assert.assertEquals("postal", organization.getPostalCode(),
            organization2.getPostalCode());
    }

    @Test
    public void testAggregateOrganizationBind() throws InvalidNameException
    {
        final LdapName ldapName = new LdapName(
            "o=New Organization2,ou=businesses,dc=example,dc=com");

        final ILdapOrganization organization = new LdapAggregation();
        organization.setDn(ldapName);
        organization.setOrganization("New Organization2", 0);
        organization.setLocality("Some Town", 0);
        organization.setTelephoneNumber("(123) 555-5555", 0);
        organization.setStreet("123 Ldap Way", 0);
        organization.setPostalCode("A1A 2B2", 0);
        manager.bind(organization);

        // just for kicks, we'll load it back as LdapOrganization rather than
        // LdapAggregation(), and it should be the same result.
        final ILdapOrganization organization2 =
            (ILdapOrganization) manager.find(LdapOrganization.class, ldapName);
        Assert.assertEquals("dn", organization.getDn(), organization2.getDn());
        Assert.assertEquals("organization", organization.getOrganization(),
            organization2.getOrganization());
        Assert.assertEquals("locality", organization.getLocality(),
            organization2.getLocality());
        Assert.assertEquals("telephone", organization.getTelephoneNumber(),
            organization2.getTelephoneNumber());
        Assert.assertEquals("street", organization.getStreet(),
            organization2.getStreet());
        Assert.assertEquals("postal", organization.getPostalCode(),
            organization2.getPostalCode());
    }

    @Test
    public void testForeignAggregateBinding() throws InvalidNameException
    {
        final LdapName ldapName = new LdapName(
            "o=Pulp Mill.,ou=businesses,dc=example,dc=com");
        final ILdapOrganization ldapEntry = (ILdapOrganization) manager.find(
            LdapOrganization.class, ldapName);

        ldapEntry.setOrganization("Pulp Mill2", 0);
        ldapEntry.setDn(new LdapName(
            "o=Pulp Mill2.,ou=businesses,dc=example,dc=com"));
        manager.bind(ldapEntry);
    }
}
