/* ###
 * IP: GHIDRA
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
package help.screenshot;

import java.awt.Component;
import java.awt.Window;
import java.io.*;
import java.util.*;

import javax.swing.*;

import org.junit.Test;

import docking.DialogComponentProvider;
import docking.action.DockingActionIf;
import docking.widgets.table.GTable;
import docking.widgets.tree.GTree;
import docking.widgets.tree.GTreeNode;
import generic.jar.ResourceFile;
import generic.util.Path;
import ghidra.app.plugin.core.console.ConsoleComponentProvider;
import ghidra.app.plugin.core.osgi.BundleStatusComponentProvider;
import ghidra.app.plugin.core.script.*;
import ghidra.app.script.*;
import ghidra.app.services.ConsoleService;
import ghidra.jython.JythonScriptProvider;
import ghidra.util.HelpLocation;

public class GhidraScriptMgrPluginScreenShots extends GhidraScreenShotGenerator {

	public GhidraScriptMgrPluginScreenShots() {
		super();
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
	}

	@Test
	public void testNew_Script_Editor() throws Exception {

		performAction("New", "GhidraScriptMgrPlugin", false);

		JDialog d = waitForJDialog("New Script: Type");
		pressButtonByText(d, "OK");

		d = waitForJDialog("New Script");
		pressButtonByText(d, "OK");

		waitForSwing();

		DialogComponentProvider dialog = waitForDialogComponent("Script Path Added/Enabled");
		close(dialog);

		captureIsolatedProvider(GhidraScriptEditorComponentProvider.class, 597, 600);
	}

	@Test
	public void testSaveAs() throws Exception {

		final ResourceFile scriptFile = createHelloWorldScript("HelloWorldScript");
		final GhidraScriptComponentProvider provider =
			showProvider(GhidraScriptComponentProvider.class);
		runSwing(() -> {

			HelpLocation helpLocation = new HelpLocation("", "");
			List<ResourceFile> scriptDirs = new ArrayList<>();
			scriptDirs.add(new ResourceFile("/User/home/ghidra_scripts"));

			SaveDialog dialog = new SaveDialog(tool.getToolFrame(), "Save Script", provider,
				scriptDirs, scriptFile, helpLocation);

			tool.showDialog(dialog);
		}, false);

		SaveDialog dialog = waitForDialogComponent(SaveDialog.class);
		captureDialog(dialog);
	}

	@Test
	public void testAssign_Key_Binding() throws Exception {

		GhidraScriptComponentProvider provider = showProvider(GhidraScriptComponentProvider.class);
		GTable scriptTable = provider.getTable();
		selectRow(scriptTable, "HelloWorldScript.java");

		performAction("Key Binding", "GhidraScriptMgrPlugin", false);
		captureDialog();
	}

	@Test
	public void testSelect_Font() throws Exception {

		GhidraScriptComponentProvider provider = showProvider(GhidraScriptComponentProvider.class);
		GTable scriptTable = provider.getTable();

		selectRow(scriptTable, "HelloWorldScript.java");
		performAction("Edit", "GhidraScriptMgrPlugin", false);

		performAction("Select Font", "GhidraScriptMgrPlugin", false);
		captureDialog();
	}

	@Test
	public void testScript_Dirs() throws Exception {
		List<ResourceFile> bundleFiles = new ArrayList<>();
		bundleFiles.add(Path.fromPathString("$USER_HOME/ghidra_scripts"));
		bundleFiles.add(Path.fromPathString("$GHIDRA_HOME/Features/Base/ghidra_scripts"));
		bundleFiles.add(Path.fromPathString("/User/defined/invalid/directory"));

		BundleStatusComponentProvider bundleStatusComponentProvider =
			showProvider(BundleStatusComponentProvider.class);

		bundleStatusComponentProvider.setBundleFilesForTesting(bundleFiles);

		waitForComponentProvider(BundleStatusComponentProvider.class);
		captureComponent(bundleStatusComponentProvider.getComponent());
	}

	@Test
	public void testEdit_Script() throws Exception {

		createHelloWorldScript("MyHelloWorldScript");
		GhidraScriptComponentProvider provider = showProvider(GhidraScriptComponentProvider.class);

		performAction("Edit", "GhidraScriptMgrPlugin", false);
		waitForSwing();

		moveProviderToFront(provider, 557, 378);
		captureProvider(provider);
	}

	@Test
	public void testScript_Manager() {
		GhidraScriptComponentProvider provider = showProvider(GhidraScriptComponentProvider.class);
		JComponent component = provider.getComponent();
		final JSplitPane splitPane =
			(JSplitPane) findComponentByName(component, "dataDescriptionSplit");
		runSwing(() -> splitPane.setDividerLocation(0.63));

		GTable scriptTable = provider.getTable();
		GTree scriptCategoryTree = provider.getTree();

		selectPath(scriptCategoryTree, "Scripts", "Examples");
		collapse(scriptCategoryTree, "Examples");// don't open examples (silly JTree)

		selectRow(scriptTable, "HelloWorldScript.java");
		scriptTable.scrollToSelectedRow();

		moveProviderToFront(provider, 1333, 570);
		captureProvider(provider);
	}

	@Test
	public void testConsole() throws Exception {
		ConsoleService service = tool.getService(ConsoleService.class);

		//@formatter:off
		service.addErrorMessage("",
			"/User/home/ghidra_scripts/HellowWorldScript1.java:29: ';' expected\n"+
				"\t}\n"+
				"\t^\n"+
				"1 error\n" +
				"> Unable to compile script class: HellowWorldScript1.java\n");
		//@formatter:on

		ConsoleComponentProvider provider = showProvider(ConsoleComponentProvider.class);
		moveProviderToFront(provider, 700, 225);
		captureProvider(provider);
	}

	@Test
	public void testDelete_Script_Confirm() throws Exception {
		createHelloWorldScript("FooScript");

		GhidraScriptComponentProvider provider = showProvider(GhidraScriptComponentProvider.class);
		GTable scriptTable = provider.getTable();
		selectRow(scriptTable, "FooScript.java");
		performAction("Delete", "GhidraScriptMgrPlugin", false);
		captureDialog();
	}

	@Test
	public void testRename() throws Exception {

		final ResourceFile scriptFile = createHelloWorldScript("HelloWorldScript");
		final GhidraScriptComponentProvider provider =
			showProvider(GhidraScriptComponentProvider.class);

		runSwing(() -> {

			HelpLocation helpLocation = new HelpLocation("", "");
			List<ResourceFile> scriptDirs = new ArrayList<>();
			scriptDirs.add(new ResourceFile("/User/home/ghidra_scripts"));

			SaveDialog dialog = new SaveDialog(tool.getToolFrame(), "Rename Script", provider,
				scriptDirs, scriptFile, helpLocation);

			tool.showDialog(dialog);
		}, false);

		SaveDialog dialog = waitForDialogComponent(SaveDialog.class);
		captureDialog(dialog);
	}

	@Test
	public void testPick() {

		List<GhidraScriptProvider> items = new ArrayList<>();
		JavaScriptProvider javaScriptProvider = new JavaScriptProvider();
		items.add(javaScriptProvider);
		items.add(new JythonScriptProvider());
		final PickProviderDialog pickDialog = new PickProviderDialog(items, javaScriptProvider);
		runSwing(() -> tool.showDialog(pickDialog), false);

		PickProviderDialog dialog = waitForDialogComponent(PickProviderDialog.class);
		captureDialog(dialog);
	}

	@Test
	public void testScriptQuickLaunchDialog() {

		DockingActionIf action = getAction(tool, "Script Quick Launch");
		performAction(action, false);
		ScriptSelectionDialog dialog = waitForDialogComponent(ScriptSelectionDialog.class);

		JTextField textField = findComponent(dialog.getComponent(), JTextField.class);
		triggerText(textField, "Hello*Pop");

		// note: textField is an instance of DropDownSelectionTextField
		waitFor(() -> (Window) getInstanceField("matchingWindow", textField));

		JComponent component = dialog.getComponent();
		Window dataTypeDialog = windowForComponent(component);
		Window[] popUpWindows = dataTypeDialog.getOwnedWindows();

		List<Component> dataTypeWindows = new ArrayList<>(Arrays.asList(popUpWindows));
		dataTypeWindows.add(dataTypeDialog);

		captureComponents(dataTypeWindows);
		closeAllWindows();
	}

//==================================================================================================
// Private Methods
//==================================================================================================

	private void collapse(final GTree tree, final String nodeName) {
		runSwing(() -> {
			GTreeNode rootNode = tree.getViewRoot();
			GTreeNode exmaplesNode = rootNode.getChild(nodeName);
			tree.collapseAll(exmaplesNode);
		});
	}

	private ResourceFile createTempScriptFile(String name) {
		File userScriptsDir = new File(GhidraScriptUtil.USER_SCRIPTS_DIR);

		if (name.length() > 50) {
			// too long and the script manager complains
			name = name.substring(name.length() - 50);
		}

		File tempFile = new File(userScriptsDir + File.separator + name + ".java");
		tempFile.deleteOnExit();
		return new ResourceFile(tempFile);
	}

	private void writeStringToFile(ResourceFile file, String string) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(file.getFile(false)));
		writer.write(string);
		writer.close();
	}

	private ResourceFile createHelloWorldScript(String name) throws Exception {
		ResourceFile newScriptFile = createTempScriptFile(name);
		String filename = newScriptFile.getName();
		String className = filename.replaceAll("\\.java", "");

		//@formatter:off
		String newScript = "//Writes \"Hello World\" to console.\n" +
				"//@category    Examples.Test\n" +
				"//@menupath    Help.Examples.Hello World\n" +
				"//@keybinding  ctrl shift COMMA\n" +
				"//@toolbar     world.png\n\n" +
				"import ghidra.app.script.GhidraScript;\n\n" +
				"public class "+className+" extends GhidraScript {\n" +

				"	@Override\n" +
				"	public void run() throws Exception {\n" +
				"		println(\"Hello World\");\n" +
				"	}\n" +
				"}\n";

		//@formatter:on

		writeStringToFile(newScriptFile, newScript);

		return newScriptFile;
	}
}
