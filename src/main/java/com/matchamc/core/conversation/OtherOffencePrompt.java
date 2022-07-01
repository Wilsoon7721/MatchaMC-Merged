package com.matchamc.core.conversation;

import java.util.UUID;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import com.matchamc.core.bukkit.util.PlayerRegistrar;
import com.matchamc.core.bukkit.util.Report;
import com.matchamc.core.bukkit.util.Reports;
import com.matchamc.shared.MsgUtils;

public class OtherOffencePrompt extends StringPrompt {
	private Reports reports;
	private PlayerRegistrar registrar;
	private UUID against;
	private boolean priority;

	public OtherOffencePrompt(Reports reports, PlayerRegistrar registrar, UUID against, boolean priority) {
		this.reports = reports;
		this.registrar = registrar;
		this.against = against;
		this.priority = priority;
	}

	@Override
	public String getPromptText(ConversationContext context) {
		return "Please input the network rule that you think this player broke.";
	}

	@Override
	public Prompt acceptInput(ConversationContext context, String input) {
		Player player = (Player) context.getForWhom();
		if(input.equalsIgnoreCase("cancel")) {
			player.sendMessage(MsgUtils.color("&cAction cancelled."));
			reports.getQueuedReports().remove(player.getUniqueId());
			return Prompt.END_OF_CONVERSATION;
		}
		Report report = reports.createReport(player.getUniqueId(), against, input, priority);
		String againstName = registrar.getNameFromRegistrar(against);
		player.sendMessage(MsgUtils.color("&eReport #" + report.getId() + " | You have reported &a" + againstName + " &efor &a" + input + "&e."));
		reports.setOnCooldown(player.getUniqueId());
		return Prompt.END_OF_CONVERSATION;
	}

}
