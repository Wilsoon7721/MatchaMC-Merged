package com.matchamc.core.conversation;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import com.matchamc.core.bukkit.util.Report;
import com.matchamc.shared.MsgUtils;

public class StatusMessagePrompt extends StringPrompt {
	private Report report;
	private Report.Status status;

	public StatusMessagePrompt(Report report, Report.Status status) {
		this.report = report;
		this.status = status;
	}

	@Override
	public String getPromptText(ConversationContext context) {
		return "Provide a message to show to the reporter for changing the report status to " + status.name().toUpperCase() + " (Input 'default' for a default message).";
	}

	@Override
	public Prompt acceptInput(ConversationContext context, String input) {
		Player p = ((Player) context.getForWhom());
		if(input.equalsIgnoreCase("default"))
			if(status == Report.Status.RESOLVED)
				input = "Thank you for your report. However, there was not enough evidence to punish this player for the reason provided.";
			else
				input = "Thank you for your report. The player has been dealt with accordingly.";
		report.setStatus(status);
		report.setReportClosedBy(p.getUniqueId());
		report.setStatusMessage(input);
		report.flush();
		p.sendMessage(MsgUtils.color("&eReport &a#" + report.getId() + " &ehas been set to &a" + status.name().toUpperCase() + " &ewith status message: &a" + input + "&e."));
		return null;
	}
}
