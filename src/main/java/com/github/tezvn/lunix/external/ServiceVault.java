package com.github.tezvn.lunix.external;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public class ServiceVault {

	private static Economy ECONOMY;

	public static boolean registerEconomy() {
		RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
		if (rsp == null)
			return false;
		ECONOMY = rsp.getProvider();
		return true;
	}

	private static Economy getEconomy() {
		return ECONOMY;
	}

	public static void withdraw(OfflinePlayer player, double amount) {
		if(ECONOMY == null)
			return;
		ECONOMY.withdrawPlayer(player, amount);
	}

	public static void deposit(OfflinePlayer player, double amount) {
		if(ECONOMY == null)
			return;
		ECONOMY.depositPlayer(player, amount);
	}

	public static double getBalance(OfflinePlayer player) {
		return ECONOMY == null ? 0 : ECONOMY.getBalance(player);
	}

}
