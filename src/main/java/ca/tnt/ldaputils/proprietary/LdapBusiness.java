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
package ca.tnt.ldaputils.proprietary;

import ca.tnt.ldaputils.annotations.LdapAttribute;
import ca.tnt.ldaputils.annotations.LdapEntity;
import ca.tnt.ldaputils.impl.LdapOrganization;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * <p/>
 * Created :  16-Apr-2006 10:47:22 PM MST
 *
 * @author Trenton D. Adams <trent.nospam@telus.net>
 */
@LdapEntity(
    requiredObjectClasses = {"tntbusiness"})
public class LdapBusiness extends LdapOrganization
    implements ILdapBusiness
{
    private static final Logger logger = Logger.getLogger(
        LdapBusiness.class);
    @LdapAttribute(name = "businessContact")
    private String businessContact;

    private String[] labeledURI;

    @LdapAttribute(name = "mail")
    private List<String> mail;

    public LdapBusiness()
    {
        super();
        mail = new ArrayList();
    }

    public String getBusinessContact()
    {
        return businessContact;
    }

    public String[] getLabeledURI()
    {
        return labeledURI;
    }

    public String getMail()
    {
        return mail != null && mail.size() > 0 ? mail.get(0) : null;
    }

    @Override
    public String[] getMails()
    {
        return mail.toArray(new String[]{});
    }

    public void setBusinessContact(final String businessContact, final int operation)
    {
        modifyBatchAttribute(operation, "businessContact", businessContact);
        this.businessContact = businessContact;
    }

    protected void setLabeledURI(String labeledURI)
    {
        logger.info("labeledURI: " + labeledURI);
        if (labeledURI != null)
        {
            final String[] webComponents;
            this.labeledURI = new String[2];
            webComponents = labeledURI.split(" ", 2);
            switch (webComponents.length)
            {
                case 0:
                    labeledURI = null;
                    break;
                case 1:
                    logger.debug("labeledURI: " + labeledURI);
                    this.labeledURI[0] = webComponents[0].matches(
                        "http:\\/\\/") ? ("http://" + webComponents[0]) :
                        webComponents[0];
                    this.labeledURI[1] = getOrganization();
                    break;
                case 2:
                    this.labeledURI[0] = webComponents[0].matches(
                        "http:\\/\\/") ? ("http://" + webComponents[0]) :
                        webComponents[0];
                    this.labeledURI[1] = webComponents[1];
                    break;
            }
        }
        else
        {
            this.labeledURI = new String[2];
        }
        logger.info("labeledURI[0]: " + this.labeledURI[0]);
        logger.info("labeledURI[1]: " + this.labeledURI[1]);
    }

    public void setLabeledURI(final String labeledURI, final int operation)
    {
        setLabeledURI(labeledURI);
        modifyBatchAttribute(operation, "labeledURI", this.labeledURI[0] + " " +
            this.labeledURI[1]);
    }

    public void setMail(final String mail, final int operation)
    {
        modifyBatchAttribute(operation, "mail", mail);
//        this.mail = mail;
    }

    @Override
    public String toString()
    {
        return super.toString() + ", LdapBusiness{" +
            "businessContact='" + businessContact + '\'' +
            ", labeledURI=" + (labeledURI == null ? null : Arrays.asList(
            labeledURI)) +
            ", mail=" + mail +
            '}';
    }
}
