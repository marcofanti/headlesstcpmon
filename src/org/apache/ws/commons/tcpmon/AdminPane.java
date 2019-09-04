/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ws.commons.tcpmon;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * this is the admin page
 */
class AdminPane extends JPanel {
	
    /**
     * Field hostLabel, tportLabel
     */
    public JLabel hostLabel, tportLabel;

    /**
     * Field port
     */
    public NumberField port;

    /**
     * Field host
     */
    public HostnameField host;

    /**
     * Field tport
     */
    public NumberField tport;

    /**
     * Field noteb
     */
    public JTabbedPane noteb;

    /**
     * Constructor AdminPage
     *
     * @param notebook
     * @param name
     */
    public AdminPane(JTabbedPane notebook, String name) {
        JPanel mainPane = null;
        JButton addButton = null;
        this.setLayout(new BorderLayout());
        noteb = notebook;
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        mainPane = new JPanel(layout);
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        mainPane.add(new JLabel(TCPMon.getMessage("newTCP00",
                "Create a new TCPMon...")
                + " "), c);

        // Add some blank space
        mainPane.add(Box.createRigidArea(new Dimension(1, 5)), c);

        // The listener info
        // /////////////////////////////////////////////////////////////////
        JPanel tmpPanel = new JPanel(new GridBagLayout());
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = 1;
        tmpPanel.add(new JLabel(TCPMon.getMessage("listenPort00",
                "Listen Port #")
                + " "), c);
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        tmpPanel.add(port = new NumberField(4), c);
        mainPane.add(tmpPanel, c);
        mainPane.add(Box.createRigidArea(new Dimension(1, 5)), c);

        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = GridBagConstraints.REMAINDER;
 
        // Target Host/Port section
        // /////////////////////////////////////////////////////////////////
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        final String listener = TCPMon.getMessage("listener00", "Listener");
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = 1;
        mainPane.add(Box.createRigidArea(new Dimension(25, 0)));
        mainPane.add(hostLabel =
                new JLabel(TCPMon.getMessage("targetHostname00",
                        "Target Hostname")
                + " "), c);
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        host = new HostnameField(30);
        mainPane.add(host, c);
        host.setText(TCPMon.DEFAULT_HOST);
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = 1;
        mainPane.add(Box.createRigidArea(new Dimension(25, 0)));
        mainPane.add(tportLabel =
                new JLabel(TCPMon.getMessage("targetPort00", "Target Port #")
                + " "), c);
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        tport = new NumberField(4);
        mainPane.add(tport, c);
        tport.setValue(TCPMon.DEFAULT_PORT);

        // Act as proxy section
        // /////////////////////////////////////////////////////////////////
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        final String proxy = TCPMon.getMessage("proxy00", "Proxy");
        // Spacer
        // ///////////////////////////////////////////////////////////////
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        mainPane.add(Box.createRigidArea(new Dimension(1, 10)), c);

        // Options section
        // /////////////////////////////////////////////////////////////////
        JPanel opts = new JPanel(new GridBagLayout());
        opts.setBorder(new TitledBorder(TCPMon.getMessage("options00",
                "Options")));
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        mainPane.add(opts, c);

        // HTTP Proxy Support section
        // /////////////////////////////////////////////////////////////////
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = GridBagConstraints.REMAINDER;

        // Spacer
        // ////////////////////////////////////////////////////////////////
        mainPane.add(Box.createRigidArea(new Dimension(1, 10)), c);

        // ADD Button
        // /////////////////////////////////////////////////////////////////
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        final String add = TCPMon.getMessage("add00", "Add");
        mainPane.add(addButton = new JButton(add), c);
        this.add(new JScrollPane(mainPane), BorderLayout.CENTER);

        // addButton.setEnabled( false );
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (add.equals(event.getActionCommand())) {
                    String text;
                    Listener l = null;
                    int lPort;
                    lPort = port.getValue(0);
                    if (lPort == 0) {

                        // no port, button does nothing
                        return;
                    }
                    String tHost = host.getText();
                    int tPort = 0;
                    tPort = tport.getValue(0);
                  try {
                        l = new Listener(noteb, null, lPort, tHost, tPort);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    // reset the port
                    port.setText(null);
                }
            }
        });
        notebook.addTab(name, this);
        
        notebook.repaint();
        notebook.setSelectedIndex(notebook.getTabCount() - 1);
    }
    
    /**
     * a text field with a restricted set of characters
     */
    static class RestrictedTextField extends JTextField {
        /**
         * Field validText
         */
        protected String validText;

        /**
         * Constructor RestrictedTextField
         *
         * @param validText
         */
        public RestrictedTextField(String validText) {
            setValidText(validText);
        }

        /**
         * Constructor RestrictedTextField
         *
         * @param columns
         * @param validText
         */
        public RestrictedTextField(int columns, String validText) {
            super(columns);
            setValidText(validText);
        }

        /**
         * Constructor RestrictedTextField
         *
         * @param text
         * @param validText
         */
        public RestrictedTextField(String text, String validText) {
            super(text);
            setValidText(validText);
        }

        /**
         * Constructor RestrictedTextField
         *
         * @param text
         * @param columns
         * @param validText
         */
        public RestrictedTextField(String text, int columns, String validText) {
            super(text, columns);
            setValidText(validText);
        }

        /**
         * Method setValidText
         *
         * @param validText
         */
        private void setValidText(String validText) {
            this.validText = validText;
        }

        /**
         * fascinatingly, this method is called in the super() constructor,
         * meaning before we are fully initialized. C++ doesnt actually permit
         * such a situation, but java clearly does...
         *
         * @return a new document
         */
        public Document createDefaultModel() {
            return new RestrictedDocument();
        }

        /**
         * this class strips out invaid chars
         */
        class RestrictedDocument extends PlainDocument {
            /**
             * Constructs a plain text document.  A default model using
             * <code>GapContent</code> is constructed and set.
             */
            public RestrictedDocument() {
            }

            /**
             * add a string; only those chars in the valid text list are allowed
             *
             * @param offset
             * @param string
             * @param attributes
             * @throws BadLocationException
             */
            public void insertString(int offset,
                                     String string,
                                     AttributeSet attributes)
                    throws BadLocationException {
                if (string == null) {
                    return;
                }
                int len = string.length();
                StringBuffer buffer = new StringBuffer(string.length());
                for (int i = 0; i < len; i++) {
                    char ch = string.charAt(i);
                    if (validText.indexOf(ch) >= 0) {
                        buffer.append(ch);
                    }
                }
                super.insertString(offset, new String(buffer), attributes);
            }
        }    // end class NumericDocument
    }

    /**
     * because we cant use Java1.4's JFormattedTextField, here is
     * a class that accepts numbers only
     */
    static class NumberField extends RestrictedTextField {
        /**
         * Field VALID_TEXT
         */
        private static final String VALID_TEXT = "0123456789";

        /**
         * Constructs a new <code>TextField</code>.  A default model is created,
         * the initial string is <code>null</code>,
         * and the number of columns is set to 0.
         */
        public NumberField() {
            super(VALID_TEXT);
        }

        /**
         * Constructs a new empty <code>TextField</code> with the specified
         * number of columns.
         * A default model is created and the initial string is set to
         * <code>null</code>.
         *
         * @param columns the number of columns to use to calculate
         *                the preferred width; if columns is set to zero, the
         *                preferred width will be whatever naturally results from
         *                the component implementation
         */
        public NumberField(int columns) {
            super(columns, VALID_TEXT);
        }

        /**
         * get the int value of a field, any invalid (non int) field returns
         * the default
         *
         * @param def default value
         * @return the field contents
         */
        public int getValue(int def) {
            int result = def;
            String text = getText();
            if ((text != null) && (text.length() != 0)) {
                try {
                    result = Integer.parseInt(text);
                } catch (NumberFormatException e) {
                }
            }
            return result;
        }

        /**
         * set the text to a numeric value
         *
         * @param value number to assign
         */
        public void setValue(int value) {
            setText(Integer.toString(value));
        }
    }    // end class NumericTextField

    /**
     * hostname fields
     */
    static class HostnameField extends RestrictedTextField {

        // list of valid chars in a hostname

        /**
         * Field VALID_TEXT
         */
        private static final String VALID_TEXT =
                "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWZYZ-.";

        /**
         * Constructor HostnameField
         *
         * @param columns
         */
        public HostnameField(int columns) {
            super(columns, VALID_TEXT);
        }

        /**
         * Constructor HostnameField
         */
        public HostnameField() {
            super(VALID_TEXT);
        }
    }    
}

