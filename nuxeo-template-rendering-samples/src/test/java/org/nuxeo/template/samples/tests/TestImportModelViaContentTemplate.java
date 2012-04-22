/*
 * (C) Copyright 2006-20012 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 *
 */

package org.nuxeo.template.samples.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.storage.sql.SQLRepositoryTestCase;
import org.nuxeo.template.api.adapters.TemplateBasedDocument;
import org.nuxeo.template.api.adapters.TemplateSourceDocument;

public class TestImportModelViaContentTemplate extends SQLRepositoryTestCase {

    DocumentModel rootDocument;

    DocumentModel workspace;

    DocumentModel docToExport;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        deployBundle("org.nuxeo.ecm.core.api");
        deployBundle("org.nuxeo.ecm.platform.content.template");
        deployBundle("org.nuxeo.template.manager.api");
        deployBundle("org.nuxeo.template.manager");
        deployBundle("org.nuxeo.template.manager.samples");

        fireFrameworkStarted();

        openSession();
    }

    @After
    public void tearDown() throws Exception {
        closeSession();
        super.tearDown();
    }

    @Test
    public void testImportContentTemplateArchive() throws Exception {

        // check result

        StringBuffer sb = new StringBuffer();
        DocumentModelList docs = session.query("select * from Document where ecm:mixinType in ('Template','TemplateBased') order by ecm:path");
        for (DocumentModel doc : docs) {
            sb.append("path: " + doc.getPathAsString() + " type: "
                    + doc.getType() + " title:" + doc.getTitle() + " name:"
                    + doc.getName() + " uuid:" + doc.getId());
            TemplateBasedDocument templateDoc = doc.getAdapter(TemplateBasedDocument.class);
            if (templateDoc != null) {
                for (String tName : templateDoc.getTemplateNames()) {
                    sb.append(" target: " + tName + "-"
                            + templateDoc.getSourceTemplateDocRef(tName));
                    assertTrue(session.exists(templateDoc.getSourceTemplateDocRef(tName)));
                }
            } else {
                TemplateSourceDocument source = doc.getAdapter(TemplateSourceDocument.class);
                assertNotNull(source);
            }
            sb.append("\n");
        }

        String dump = sb.toString();
        System.out.println("Import completed : " + docs.size() + " docs");
        System.out.println(dump);

    }
}
