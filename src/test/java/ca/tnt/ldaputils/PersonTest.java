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

import ca.tnt.ldaputils.ldapimpl.InetOrgPerson;
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

import javax.naming.NamingException;
import javax.naming.ldap.LdapName;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
    "add-person.ldif"})
public class PersonTest extends AbstractLdapTestUnit
{
    private LdapManager manager;

    @Before
    public void setUp()
    {
        manager = new LdapManager("localhost", "" + ldapServer.getPort(),
            "uid=admin,ou=system", "secret");
    }

    @Test
    public void testLdapOrg()
        throws NamingException, NoSuchAlgorithmException
    {
        final LdapName ldapName = new LdapName(
            "uid=iop,ou=People,dc=example,dc=com");
        final InetOrgPerson ldapEntry = (InetOrgPerson) manager.find(
            InetOrgPerson.class, ldapName);
        Assert.assertEquals("photo count", 2, ldapEntry.getJpegPhotos().size());
        final MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] thedigest = md.digest(
            (byte[]) ldapEntry.getJpegPhotos().get(0));
        BigInteger bigInt = new BigInteger(1, thedigest);
        String hashtext = bigInt.toString(16);

        while (hashtext.length() < 32)
        {  // Now we need to zero pad it if you actually want the full 32 chars.
            hashtext = "0" + hashtext;
        }
        Assert.assertEquals("md5", "c000a2b462f78d5e7d22c43eb7d346cc",
            hashtext);


        thedigest = md.digest(
            (byte[]) ldapEntry.getJpegPhotos().get(1));
        bigInt = new BigInteger(1, thedigest);
        hashtext = bigInt.toString(16);

        while (hashtext.length() < 32)
        {  // Now we need to zero pad it if you actually want the full 32 chars.
            hashtext = "0" + hashtext;
        }
        Assert.assertEquals("md5", "516b9132d13e43b4b6af84b5e35a1109",
            hashtext);
    }

}
