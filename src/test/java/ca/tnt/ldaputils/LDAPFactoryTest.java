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

import ca.tnt.ldaputils.exception.LdapNamingException;
import ca.tnt.ldaputils.impl.LDAPEntryImpl;
import ca.tnt.ldaputils.impl.LdapEntry;
import ca.tnt.ldaputils.proprietary.ILdapBusiness;
import ca.tnt.util.Property;
import junit.framework.TestCase;
import org.apache.log4j.Logger;

import javax.naming.InvalidNameException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * <p/>
 * Created :  Mar 23, 2008 6:37:56 PM MST
 * <p/>
 * Modified : $Date$
 * <p/>
 * Revision : $Revision$
 * <p/>
 * TODO: change modifyBatchAttribute to setAttribute and saveChanges?
 *
 * @author trenta
 */
public class LDAPFactoryTest extends TestCase
{
    static Logger logger = Logger.getLogger(LDAPFactoryTest.class);
    Properties loadedProperties;
    LdapName ldapBaseDN;
    String ldapHost;
    String ldapPort;
    LdapName ldapManagerDN;
    String ldapManagerPW;

    /**
     * Found during testSearch(), used in testModify()
     */
    ILdapBusiness ldapObject;

    protected void setUp() throws Exception
    {
        super.setUp();
        loadedProperties = Property.loadProperties("/ldap.properties");
        ldapHost = loadedProperties.getProperty("LDAP.host");
        ldapPort = loadedProperties.getProperty("LDAP.port");
        ldapBaseDN = new LdapName(loadedProperties.getProperty("LDAP.baseDN"));
        ldapManagerDN = new LdapName(loadedProperties.getProperty(
            "LDAP.managerdn"));
        ldapManagerPW = loadedProperties.getProperty("LDAP.managerpw");

    }

/*    protected void setupApacheDS()
{
    // Add partition 'sevenSeas'
    MutablePartitionConfiguration pcfg = new MutablePartitionConfiguration();
    pcfg.setName( "sevenSeas" );
    pcfg.setSuffix( "o=sevenseas" );

    // Create some indices
    Set<String> indexedAttrs = new HashSet<String>();
    indexedAttrs.add( "objectClass" );
    indexedAttrs.add( "o" );
    pcfg.setIndexedAttributes( indexedAttrs );

    // Create a first entry associated to the partition
    Attributes attrs = new BasicAttributes( true );

    // First, the objectClass attribute
    Attribute attr = new BasicAttribute( "objectClass" );
    attr.add( "top" );
    attr.add( "organization" );
    attrs.put( attr );

    // The the 'Organization' attribute
    attr = new BasicAttribute( "o" );
    attr.add( "sevenseas" );
    attrs.put( attr );

    // Associate this entry to the partition
    pcfg.setContextEntry( attrs );

    // As we can create more than one partition, we must store
    // each created partition in a Set before initialization
    Set<MutablePartitionConfiguration> pcfgs = new HashSet<MutablePartitionConfiguration>();
    pcfgs.add( pcfg );

    configuration.setContextPartitionConfigurations( pcfgs );

    // Create a working directory
    File workingDirectory = new File( "server-work" );
    configuration.setWorkingDirectory( workingDirectory );

    // Now, let's call the upper class which is responsible for the
    // partitions creation
    super.setUp();
}*/

    public void loadPropertyTest()
    {
        assertNotNull(loadedProperties);
        assertNotNull(ldapHost);
        assertNotNull(ldapPort);
        assertNotNull(ldapBaseDN);
        assertNotNull(ldapManagerDN);
        assertNotNull(ldapManagerPW);
    }

    public void searchTest(final String filter, final String keyAttribute)
    {
        final Map sortedEntries;
        final String[] attributes;

        attributes = null;  // grab all attributes
        final LdapManager manager = new LdapManager();

        try
        {
            sortedEntries =
                manager.find(ldapBaseDN, filter, keyAttribute, attributes,
                    LdapEntry.class, LdapManager.SORTED_ORDER,
                    SearchControls.SUBTREE_SCOPE);
            final Iterator entryIt = sortedEntries.values().iterator();
            assertNotNull(sortedEntries);
            assertEquals(sortedEntries.size(), 1);
            while (entryIt.hasNext())
            {
                ldapObject = (ILdapBusiness) entryIt.next();
                logger.debug(ldapObject);
                assertNotNull(ldapObject);
            }
        }
        catch (LdapNamingException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Tests using the ADD_ATTRIBUTE capability.
     */
    public void testAdd()
    {
        try
        {
            loadPropertyTest();
            searchTest("o=" + Rdn.escapeValue("Bilsky,  B & R Trucking"),
                "o");
            logger.info("adding mail trent@example.com");
            ldapObject.modifyBatchAttribute(LDAPEntryImpl.ADD_ATTRIBUTE,
                "mail", "trent@example.com");
            ldapObject.modifyBatchAttributes();

            searchTest("o=" + Rdn.escapeValue("Bilsky,  B & R Trucking"),
                "o");
            showObject();
        }
        catch (LdapNamingException ldapNamingException)
        {
            logger.error(ldapNamingException);
            assertNull(ldapNamingException);
        }
    }

    /**
     * Tests using the REMOVE_ATTRIBUTE capability.
     */
    public void testRemove()
    {
        try
        {
            loadPropertyTest();
            searchTest("o=" + Rdn.escapeValue("Bilsky,  B & R Trucking"),
                "o");
            logger.info("removing mail trent@example.com");
            ldapObject.modifyBatchAttribute(LDAPEntryImpl.REMOVE_ATTRIBUTE,
                "mail", "trent@example.com");
            ldapObject.modifyBatchAttributes();

            searchTest("o=" + Rdn.escapeValue("Bilsky,  B & R Trucking"),
                "o");
            showObject();
        }
        catch (LdapNamingException ldapNamingException)
        {
            logger.error(ldapNamingException);
            assertNull(ldapNamingException);
        }

        // TODO : Must assert something
    }

    /**
     * Tests using the REPLACE_ATTRIBUTE capability.
     */
    public void testReplace()
    {
        try
        {
            loadPropertyTest();
            searchTest("o=" + Rdn.escapeValue("Bilsky,  B & R Trucking"),
                "o");
            logger.info("replacing mail trent@example.com");
            final String oldMail;
            oldMail = ldapObject.getMail();
            ldapObject.modifyBatchAttribute(LDAPEntryImpl.REPLACE_ATTRIBUTE,
                "mail", "trent@example.com" );
            ldapObject.modifyBatchAttributes();

            searchTest("o=" + Rdn.escapeValue("Bilsky,  B & R Trucking"),
                "o");
            showObject();
            assertEquals(ldapObject.getMail(), "trent@example.com");

            logger.info("reverting mail");
            ldapObject.modifyBatchAttribute(LDAPEntryImpl.REPLACE_ATTRIBUTE,
                "mail", oldMail );
            ldapObject.modifyBatchAttributes();

            searchTest("o=" + Rdn.escapeValue("Bilsky,  B & R Trucking"),
                "o");
            showObject();
            assertEquals(ldapObject.getMail(), oldMail);
        }
        catch (LdapNamingException ldapNamingException)
        {
            logger.error(ldapNamingException);
            assertNull(ldapNamingException);
        }
    }

    /**
     * Shows the current ldapObject mail attribute.
     */
    private void showObject()
    {
        final List attributeValues;
        attributeValues =
            ldapObject.getAttributeValues("mail");
        for (final Object attributeValue : attributeValues)
        {   // print out all the new values
            logger.info("  mail : " + attributeValue);
        }
    }

    public void testBind()
    {
        final Attributes attributes;
        attributes = new BasicAttributes();
        BasicAttribute currentAttribute;


        currentAttribute = new BasicAttribute("objectClass");
        currentAttribute.add("top");
        currentAttribute.add("organization");
        currentAttribute.add("tntbusiness");
        currentAttribute.add("labeledURIObject");
        attributes.put(currentAttribute);

        currentAttribute = new BasicAttribute("o");
        currentAttribute.add("TestBus");
        attributes.put("o", "TestBus");
        final ILdapEntry ldapEntry;
        final LdapManager manager = new LdapManager();
        try
        {
            final LdapName testBus;
            testBus = (LdapName) ldapBaseDN.clone();
            testBus.add("ou=businesses");
            testBus.add("o=TestBus");
            logger.info("binding object to LDAP: o=TestBus,ou=businesses...");
/*            ldapEntry = manager.createInstance(attributes,
                null, testBus);*/
            ldapEntry = null;
            manager.bind(ldapEntry);
            searchTest("o=TestBus", "o");
            assertEquals(this.ldapObject.getOrganization(),
                ((ILdapBusiness) ldapEntry).getOrganization());
            logger.info("  bind success");
            manager.unbind(ldapEntry);
            logger.info("  unbind success");
//            assertEquals(this.ldapObject, ldapObject);
        }
        catch (LdapNamingException e)
        {
            assertNull(e);
            logger.error(e);
        }
        catch (InvalidNameException e)
        {
            fail("name error");
        }
    }

    public void testToString()
    {
        loadPropertyTest();
        searchTest("o=" + Rdn.escapeValue("Bilsky,  B & R Trucking"),
            "o");
        logger.info(ldapObject);
    }

}
