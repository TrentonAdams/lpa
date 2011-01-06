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
    public void testBind() throws InvalidNameException
    {
        final LdapName ldapName = new LdapName(
            "o=New Organization,ou=businesses,dc=example,dc=com");

        final ILdapOrganization organization = new LdapOrganization();
        organization.setDn(ldapName);
        organization.setOrganization("New Organization", 0);
        organization.setLocality("Some Town", 0);
        organization.setTelephoneNumber("(123) 555-5555", 0);
        organization.setStreet("123 Ldap Way", 0);
        organization.setPostalCode("A1A 2B2", 0);
        manager.bind(organization);

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
}
