DELETE FROM 'Webhook'
WHERE guildId IS %s AND channelId IS %s AND id NOT IN (%s);