package test.eufonia;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.eufonia.mod.commands.ChestlootCommand;
import test.eufonia.mod.commons.LootTableConfig;

public class EufoniaTest implements ModInitializer {
	public static final String MOD_ID = "eufonia-test";
	public static final String TEXT_USED_CHEST = "Cofre usado";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LootTableConfig.loadConfig();
		new ChestlootCommand();
	}
}