package me.uniodex.uniolobi.managers;

import me.clip.placeholderapi.PlaceholderAPI;
import me.uniodex.uniolobi.Main;
import me.uniodex.uniolobi.utils.ItemStackBuilder;
import me.uniodex.uniolobi.utils.Utils;
import me.uniodex.uniolobi.utils.packages.itembuilder.ItemBuilder;
import me.uniodex.uniolobi.utils.packages.menubuilder.inventory.InventoryMenuBuilder;
import me.uniodex.uniolobi.utils.packages.menubuilder.inventory.ItemListener;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

@SuppressWarnings("deprecation")
public class MenuManager {

    private Main plugin;

    public MenuManager(Main plugin) {
        this.plugin = plugin;
    }

    public void openMenu(Player p, InventoryMenuBuilder menu) {
        if (p != null && menu != null && menu.build() != null) {
            p.closeInventory();
            menu.show(p);
        }
    }

    public Inventory getMarket() {
        return plugin.getManager().skywarsMarketManager.mainMarketMenu().build();
    }

    public Inventory getProfile(Player p) {
        InventoryMenuBuilder menu = new InventoryMenuBuilder(45, "§1Profil ve Ayarlar");
        ArrayList<String> lorelist = new ArrayList<String>();
        lorelist.add(PlaceholderAPI.setPlaceholders(p, "§7Cevher Miktarı: §e%gadgetsmenu_mystery_dust%"));
        lorelist.add(PlaceholderAPI.setPlaceholders(p, "§7Gizemli Kutular: §e%gadgetsmenu_mystery_boxes%"));
        lorelist.add("§7Verilen Toplam Oy: §e" + plugin.getManager().sqlManager.getPlayerVoteCount(p.getName()));
        //lorelist.add(PlaceholderAPI.setPlaceholders(p,"§7Toplanan Hazine: §e%mvdw_treasurehunt_user_count%/%mvdw_treasurehunt_total_count%"));
        lorelist.add("§7Toplanan Hazine: §e0/25");
        lorelist.add(PlaceholderAPI.setPlaceholders(p, "§7Rütbe: §e%mvdw_group%"));
        ItemStack skull = new ItemBuilder(Utils.getSkull(p.getName(), "§aHesap Bilgileri")).buildMeta().withLore(lorelist).item().build();
        ItemStack emptyItem = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 1).buildMeta().withDisplayName("§a").item().build();

        menu.withItem(4, skull);
        for (int i = 9; i < 18; i++) {
            menu.withItem(i, emptyItem);
        }

        ItemStack statsItem = new ItemBuilder(Material.PAPER, 1).buildMeta().withDisplayName("§aİstatistikler").withLore("§7Oyunlardaki istatistiklerini", "§7görmek için tıkla!").item().build();
        menu.withItem(29, statsItem, new ItemListener() {
            @Override
            public void onInteract(Player player, ClickType action, ItemStack item) {
                player.openInventory(getStats(player));
            }
        }, InventoryMenuBuilder.ALL_CLICK_TYPES);

        ItemStack friendsItem = new ItemBuilder(Utils.getSkullwithTexture("8ff88b122ff92513c6a27b7f67cb3fea97439e078821d6861b74332a2396", "§aArkadaşlar")).buildMeta().withLore("§7Arkadaş listeni", "§7görmek için tıkla!").item().build();
        menu.withItem(30, friendsItem, new ItemListener() {
            @Override
            public void onInteract(Player player, ClickType action, ItemStack item) {
                player.performCommand("friendsgui");
            }
        }, InventoryMenuBuilder.ALL_CLICK_TYPES);

        ItemStack partyItem = new ItemBuilder(Utils.getSkullwithTexture("345b2edd9ec69a350a867db0e5b0b87551aff498a88e01e2bd6a036ff4d39", "§aParti")).buildMeta().withLore("§7Partini görmek için tıkla!").item().build();
        menu.withItem(31, partyItem, new ItemListener() {
            @Override
            public void onInteract(Player player, ClickType action, ItemStack item) {
                player.performCommand("partygui");
            }
        }, InventoryMenuBuilder.ALL_CLICK_TYPES);

        ItemStack settingsItem = new ItemBuilder(Material.REDSTONE_COMPARATOR, 1).buildMeta().withDisplayName("§aLobi Ayarları").withLore("§7Lobi ayarlarını görmek ve", "§7düzenlemek için tıkla!").item().build();
        menu.withItem(32, settingsItem, new ItemListener() {
            @Override
            public void onInteract(Player player, ClickType action, ItemStack item) {
                player.openInventory(getSettings(player));
            }
        }, InventoryMenuBuilder.ALL_CLICK_TYPES);

        ItemStack questsItem = new ItemBuilder(Material.BOOK, 1).buildMeta().withDisplayName("§aÖdüller ve Görevler").withLore("§7Ödüllerini toplamak ve", "§7görevler yapmak için tıkla!").item().build();
        menu.withItem(33, questsItem, new ItemListener() {
            @Override
            public void onInteract(Player player, ClickType action, ItemStack item) {
                player.openInventory(getRewards(player));
            }
        }, InventoryMenuBuilder.ALL_CLICK_TYPES);

        return menu.build();
    }

    public Inventory getRewards(Player p) {
        plugin.getManager().rewardManager.reloadRewards(p);
        InventoryMenuBuilder menu = new InventoryMenuBuilder(45, "§1Ödüller ve Görevler");
        ItemStack skull = new ItemBuilder(Utils.getSkull(p.getName(), "§aOyuncu Bilgileri")).buildMeta().withLore("§7Verilen Toplam Oy: §e" + plugin.getManager().sqlManager.getPlayerVoteCount(p.getName()), "§7İzlenen Toplam Reklam: §e0").item().build();
        ItemStack emptyItem = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 1).buildMeta().withDisplayName("§a").item().build();

        menu.withItem(4, skull);
        for (int i = 9; i < 18; i++) {
            menu.withItem(i, emptyItem);
        }

        String voteLink = plugin.getManager().rewardManager.getVoteLink(p.getName());
        ItemStack voteItem = new ItemBuilder(Utils.getSkullwithTexture("9193c1b1404899edea9ff813a6cb7a88d3db93a6d5b761980647c139021897", "§aOy Ver")).buildMeta().withLore("§7Sunucumuza her gün oy", "§7vererek ödüller kazan!", "", "§7Bugün §f4 §7kez oy vermişsin!", "", "§6Daha fazla ödül kazanmak", "§6için yarın tekrar oy ver!").item().build();
        if (voteLink.contains("vote1")) {
            voteItem = new ItemBuilder(Utils.getSkullwithTexture("9193c1b1404899edea9ff813a6cb7a88d3db93a6d5b761980647c139021897", "§aOy Ver")).buildMeta().withLore("§7Sunucumuza oy vererek §b200 cevher", "§7ve §b1 gizemli kutu §7kazan!", "", "§7Bugün §f0 §7kez oy vermişsin!", "", "§6Daha fazla ödül kazanmak", "§6için daha fazla oy ver!").item().build();
        } else if (voteLink.contains("vote2")) {
            voteItem = new ItemBuilder(Utils.getSkullwithTexture("9193c1b1404899edea9ff813a6cb7a88d3db93a6d5b761980647c139021897", "§aOy Ver")).buildMeta().withLore("§7Sunucumuza oy vererek §b200 cevher", "§7ve §b1 gizemli kutu §7kazan!", "", "§7Bugün §f1 §7kez oy vermişsin!", "", "§6Daha fazla ödül kazanmak", "§6için daha fazla oy ver!").item().build();
        } else if (voteLink.contains("vote3")) {
            voteItem = new ItemBuilder(Utils.getSkullwithTexture("9193c1b1404899edea9ff813a6cb7a88d3db93a6d5b761980647c139021897", "§aOy Ver")).buildMeta().withLore("§7Sunucumuza oy vererek §b300 cevher §7kazan!", "", "§7Bugün §f2 §7kez oy vermişsin!", "", "§6Daha fazla ödül kazanmak", "§6için daha fazla oy ver!").item().build();
        } else if (voteLink.contains("vote4")) {
            voteItem = new ItemBuilder(Utils.getSkullwithTexture("9193c1b1404899edea9ff813a6cb7a88d3db93a6d5b761980647c139021897", "§aOy Ver")).buildMeta().withLore("§7Sunucumuza oy vererek §b200 cevher §7kazan!", "", "§7Bugün §f3 §7kez oy vermişsin!", "", "§6Daha fazla ödül kazanmak", "§6için daha fazla oy ver!").item().build();
        }

        menu.withItem(29, voteItem, new ItemListener() {
            @Override
            public void onInteract(Player player, ClickType action, ItemStack item) {
                player.closeInventory();
                if (voteLink != "") {
                    player.sendMessage(Main.prefix + "§f§lOy vermek ve ödülleri toplamak için tıkla: §b§l§n" + voteLink);
                } else {
                    player.sendMessage(Main.prefix + "§cBugünkü oy verme haklarının tamamını kullanmışsın. Yarın daha fazla oy verebilir ve ödül kazanabilirsin!");
                }
            }
        }, InventoryMenuBuilder.ALL_CLICK_TYPES);

        ItemStack dailyReward = new ItemBuilder(Utils.getSkullwithTexture("6c8652bfdb7adde128e7eacc50d16eb9f487a3209b304de3b9697cebf13323b", "§aGünlük Ödül")).buildMeta().withLore("§7Her gün sunucumuza gir ve", "§b200 cevherlik §7ödülünü topla!", "", "§6Ödülünü almak için tıkla!").item().build();
        if (plugin.getManager().rewardManager.isDailyRewardTaken(p.getName())) {
            dailyReward = new ItemBuilder(Utils.getSkullwithTexture("6c8652bfdb7adde128e7eacc50d16eb9f487a3209b304de3b9697cebf13323b", "§aGünlük Ödül")).buildMeta().withLore("§7Her gün sunucumuza gir ve", "§b200 cevherlik §7ödülünü topla!", "", "§cÖdülünü zaten almışsın!").item().build();
        }
        menu.withItem(30, dailyReward, new ItemListener() {
            @Override
            public void onInteract(Player player, ClickType action, ItemStack item) {
                player.closeInventory();
                plugin.getManager().rewardManager.dailyReward(player.getName());
            }
        }, InventoryMenuBuilder.ALL_CLICK_TYPES);

        ItemStack dailyRewardVIP = new ItemBuilder(Utils.getSkullwithTexture("f5612dc7b86d71afc1197301c15fd979e9f39e7b1f41d8f1ebdf8115576e2e", "§bGünlük VIP Ödülü")).buildMeta().withLore("§7Her gün sunucumuza gir", "§7ve §b400 cevherlik §7ödülünü topla!", "", "§6Ödülünü almak için tıkla!").item().build();
        if (plugin.getManager().rewardManager.isDailyRewardVIPTaken(p.getName())) {
            dailyRewardVIP = new ItemBuilder(Utils.getSkullwithTexture("f5612dc7b86d71afc1197301c15fd979e9f39e7b1f41d8f1ebdf8115576e2e", "§bGünlük VIP Ödülü")).buildMeta().withLore("§7Her gün sunucumuza gir", "§7ve §b400 cevherlik §7ödülünü topla!", "", "§cÖdülünü zaten almışsın!").item().build();
        }

        if (!plugin.getManager().getPermissions().has("world", p.getName(), "unio.rank.vip")) {
            dailyRewardVIP = new ItemBuilder(Utils.getSkullwithTexture("f5612dc7b86d71afc1197301c15fd979e9f39e7b1f41d8f1ebdf8115576e2e", "§bGünlük VIP Ödülü")).buildMeta().withLore("§7Her gün sunucumuza gir", "§7ve §b400 cevherlik §7ödülünü topla!", "", "§cBu ödülü alabilmek için VIP olmalısın!").item().build();
        }

        menu.withItem(31, dailyRewardVIP, new ItemListener() {
            @Override
            public void onInteract(Player player, ClickType action, ItemStack item) {
                player.closeInventory();
                plugin.getManager().rewardManager.dailyRewardVIP(player.getName());
            }
        }, InventoryMenuBuilder.ALL_CLICK_TYPES);

        ItemStack monthlyRewardVIP = new ItemBuilder(Utils.getSkullwithTexture("96e39283e199c11363bfbd1adb47bd305090da562f3717a4f067438ba06525", "§cAylık VIP Ödülü")).buildMeta().withLore("§7Her ay sunucumuza gir, §b1000 cevher", "§7ve §b5 gizemli kutu §7ödülünü topla!", "", "§6Ödülünü almak için tıkla!").item().build();
        if (plugin.getManager().rewardManager.isMonthlyRewardVIPTaken(p.getName())) {
            monthlyRewardVIP = new ItemBuilder(Utils.getSkullwithTexture("96e39283e199c11363bfbd1adb47bd305090da562f3717a4f067438ba06525", "§cAylık VIP Ödülü")).buildMeta().withLore("§7Her ay sunucumuza gir, §b1000 cevher", "§7ve §b5 gizemli kutu §7ödülünü topla!", "", "§cÖdülünü zaten almışsın!").item().build();
        }

        if (!plugin.getManager().getPermissions().has("world", p.getName(), "unio.rank.vip")) {
            monthlyRewardVIP = new ItemBuilder(Utils.getSkullwithTexture("96e39283e199c11363bfbd1adb47bd305090da562f3717a4f067438ba06525", "§cAylık VIP Ödülü")).buildMeta().withLore("§7Her ay sunucumuza gir, §b1000 cevher", "§7ve §b5 gizemli kutu §7ödülünü topla!", "", "§cBu ödülü alabilmek için VIP olmalısın!").item().build();
        }
        menu.withItem(32, monthlyRewardVIP, new ItemListener() {
            @Override
            public void onInteract(Player player, ClickType action, ItemStack item) {
                player.closeInventory();
                plugin.getManager().rewardManager.monthlyRewardVIP(player.getName());
            }
        }, InventoryMenuBuilder.ALL_CLICK_TYPES);

        ItemStack adReward = new ItemBuilder(Utils.getSkullwithTexture("e36e94f6c34a35465fce4a90f2e25976389eb9709a12273574ff70fd4daa6852", "§6Reklam Ödülü")).buildMeta().withLore("§7Reklam izle, §b250 cevher", "§7ve §b1 gizemli kutu §7ödülünü topla!", "", "§cReklam izleme sistemi yapım aşamasındadır!").item().build();
        menu.withItem(33, adReward, new ItemListener() {
            @Override
            public void onInteract(Player player, ClickType action, ItemStack item) {
                player.closeInventory();
                player.sendMessage(Main.prefix + ChatColor.translateAlternateColorCodes('&', "&cReklam izleme sistemi yapım aşamasındadır."));
            }
        }, InventoryMenuBuilder.ALL_CLICK_TYPES);

        ItemStack backToMainMenuItem = new ItemBuilder(Material.BARRIER, 1).buildMeta().withDisplayName("§aProfile Geri Dön").withLore("§7Profil menüsüne dönmek için tıkla.").item().build();
        menu.withItem(40, backToMainMenuItem, new ItemListener() {
            @Override
            public void onInteract(Player player, ClickType action, ItemStack item) {
                player.openInventory(getProfile(player));
            }
        }, InventoryMenuBuilder.ALL_CLICK_TYPES);

        return menu.build();
    }

    private Inventory getSettings(Player p) {
        InventoryMenuBuilder menu = new InventoryMenuBuilder(45, "§1Lobi Ayarları");
        ItemStack skull = Utils.getSkull(p.getName(), "§aHesap Bilgileri");
        ItemStack emptyItem = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 1).buildMeta().withDisplayName("§a").item().build();

        menu.withItem(4, skull);
        for (int i = 9; i < 18; i++) {
            menu.withItem(i, emptyItem);
        }

        ItemStack toggleHidePlayer = new ItemBuilder(Material.WATCH, 1).buildMeta().withDisplayName("§aOyuncuları Gizle").item().build();
        menu.withItem(20, toggleHidePlayer);
        if (!plugin.getManager().sqlManager.getSettingStatus(p.getName(), "hidePlayers")) {
            ItemStack hidePlayertoggler = new ItemBuilder(Material.STAINED_CLAY, 1, 14).buildMeta().withDisplayName("§cDevre Dışı").withLore("§7Oyuncuları gizlemek için tıkla.").item().build();
            menu.withItem(29, hidePlayertoggler, new ItemListener() {
                @Override
                public void onInteract(Player player, ClickType action, ItemStack item) {
                    player.closeInventory();
                    plugin.getManager().playerManager.toggleHidePlayer(p);
                }
            }, InventoryMenuBuilder.ALL_CLICK_TYPES);
        } else {
            ItemStack hidePlayertoggler = new ItemBuilder(Material.STAINED_CLAY, 1, 13).buildMeta().withDisplayName("§aEtkin").withLore("§7Oyuncuları göstermek için tıkla.").item().build();
            menu.withItem(29, hidePlayertoggler, new ItemListener() {
                @Override
                public void onInteract(Player player, ClickType action, ItemStack item) {
                    player.closeInventory();
                    plugin.getManager().playerManager.toggleHidePlayer(p);
                }
            }, InventoryMenuBuilder.ALL_CLICK_TYPES);
        }

        ItemStack toggleDoubleJump = new ItemBuilder(Material.SLIME_BLOCK, 1).buildMeta().withDisplayName("§aÇift Zıplama").item().build();
        menu.withItem(21, toggleDoubleJump);
        if (!plugin.getManager().sqlManager.getSettingStatus(p.getName(), "doubleJump")) {
            ItemStack doubleJumpToggler = new ItemBuilder(Material.STAINED_CLAY, 1, 14).buildMeta().withDisplayName("§cDevre Dışı").withLore("§7Çift zıplamayı açmak için tıkla.").item().build();
            menu.withItem(30, doubleJumpToggler, new ItemListener() {
                @Override
                public void onInteract(Player player, ClickType action, ItemStack item) {
                    player.closeInventory();
                    plugin.getManager().playerManager.toggleDoubleJump(p);
                }
            }, InventoryMenuBuilder.ALL_CLICK_TYPES);
        } else {
            ItemStack doubleJumpToggler = new ItemBuilder(Material.STAINED_CLAY, 1, 13).buildMeta().withDisplayName("§aEtkin").withLore("§7Çift zıplamayı kapatmak için tıkla.").item().build();
            menu.withItem(30, doubleJumpToggler, new ItemListener() {
                @Override
                public void onInteract(Player player, ClickType action, ItemStack item) {
                    player.closeInventory();
                    plugin.getManager().playerManager.toggleDoubleJump(p);
                }
            }, InventoryMenuBuilder.ALL_CLICK_TYPES);
        }

        ItemStack toggleChat = new ItemBuilder(Material.PAPER, 1).buildMeta().withDisplayName("§aSohbeti Kapat").item().build();
        menu.withItem(22, toggleChat);
        if (!plugin.getManager().sqlManager.getSettingStatus(p.getName(), "closeChat")) {
            ItemStack chatToggler = new ItemBuilder(Material.STAINED_CLAY, 1, 14).buildMeta().withDisplayName("§cDevre Dışı").withLore("§7Sohbeti gizlemek için tıkla.").item().build();
            menu.withItem(31, chatToggler, new ItemListener() {
                @Override
                public void onInteract(Player player, ClickType action, ItemStack item) {
                    player.closeInventory();
                    plugin.getManager().playerManager.toggleChat(p);
                }
            }, InventoryMenuBuilder.ALL_CLICK_TYPES);
        } else {
            ItemStack chatToggler = new ItemBuilder(Material.STAINED_CLAY, 1, 13).buildMeta().withDisplayName("§aEtkin").withLore("§7Sohbeti göstermek için tıkla.").item().build();
            menu.withItem(31, chatToggler, new ItemListener() {
                @Override
                public void onInteract(Player player, ClickType action, ItemStack item) {
                    player.closeInventory();
                    plugin.getManager().playerManager.toggleChat(p);
                }
            }, InventoryMenuBuilder.ALL_CLICK_TYPES);
        }

        ItemStack toggleSitting = new ItemBuilder(Material.ACACIA_STAIRS, 1).buildMeta().withDisplayName("§aOturma").item().build();
        menu.withItem(23, toggleSitting);
        if (!plugin.getManager().sqlManager.getSettingStatus(p.getName(), "sittingEnabled")) {
            ItemStack sittingToggler = new ItemBuilder(Material.STAINED_CLAY, 1, 14).buildMeta().withDisplayName("§cDevre Dışı").withLore("§7Oturmayı etkinleştirmek için tıkla.").item().build();
            menu.withItem(32, sittingToggler, new ItemListener() {
                @Override
                public void onInteract(Player player, ClickType action, ItemStack item) {
                    player.closeInventory();
                    plugin.getManager().playerManager.toggleSitting(p);
                }
            }, InventoryMenuBuilder.ALL_CLICK_TYPES);
        } else {
            ItemStack doubleJumpToggler = new ItemBuilder(Material.STAINED_CLAY, 1, 13).buildMeta().withDisplayName("§aEtkin").withLore("§7Oturmayı devre dışı bırakmak için tıkla.").item().build();
            menu.withItem(32, doubleJumpToggler, new ItemListener() {
                @Override
                public void onInteract(Player player, ClickType action, ItemStack item) {
                    player.closeInventory();
                    plugin.getManager().playerManager.toggleSitting(p);
                }
            }, InventoryMenuBuilder.ALL_CLICK_TYPES);
        }

        ItemStack toggleStacker = new ItemBuilder(Material.SADDLE, 1).buildMeta().withDisplayName("§aTaşıma Modunu Kapat").item().build();
        menu.withItem(24, toggleStacker);
        if (!plugin.getManager().sqlManager.getSettingStatus(p.getName(), "closeStacking")) {
            ItemStack stackerToggler = new ItemBuilder(Material.STAINED_CLAY, 1, 14).buildMeta().withDisplayName("§cDevre Dışı").withLore("§7Taşıma modunu kapatmak için tıkla.").item().build();
            menu.withItem(33, stackerToggler, new ItemListener() {
                @Override
                public void onInteract(Player player, ClickType action, ItemStack item) {
                    player.closeInventory();
                    plugin.getManager().playerManager.toggleStacker(p);
                }
            }, InventoryMenuBuilder.ALL_CLICK_TYPES);
        } else {
            ItemStack stackerToggler = new ItemBuilder(Material.STAINED_CLAY, 1, 13).buildMeta().withDisplayName("§aEtkin").withLore("§7Taşıma modunu açmak için tıkla.").item().build();
            menu.withItem(33, stackerToggler, new ItemListener() {
                @Override
                public void onInteract(Player player, ClickType action, ItemStack item) {
                    player.closeInventory();
                    plugin.getManager().playerManager.toggleStacker(p);
                }
            }, InventoryMenuBuilder.ALL_CLICK_TYPES);
        }

        ItemStack backToMainMenuItem = new ItemBuilder(Material.BARRIER, 1).buildMeta().withDisplayName("§aProfile Geri Dön").withLore("§7Profil menüsüne dönmek için tıkla.").item().build();
        menu.withItem(40, backToMainMenuItem, new ItemListener() {
            @Override
            public void onInteract(Player player, ClickType action, ItemStack item) {
                player.openInventory(getProfile(player));
            }
        }, InventoryMenuBuilder.ALL_CLICK_TYPES);

        return menu.build();
    }

    private Inventory getStats(Player p) {
        InventoryMenuBuilder menu = new InventoryMenuBuilder(45, "§1İstatistikler");
        ItemStack skull = Utils.getSkull(p.getName(), "§aHesap Bilgileri");
        ItemStack emptyItem = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 1).buildMeta().withDisplayName("§a").item().build();

        menu.withItem(4, skull);
        for (int i = 9; i < 18; i++) {
            menu.withItem(i, emptyItem);
        }

        /*ItemStack sgStats = new ItemBuilder(Material.CHEST, 1).buildMeta().withDisplayName("§aSurvivalGames").withLore("§7İstatistiklerini görmek için tıkla.").item().build();
        menu.withItem(30, sgStats, new ItemListener() {
            @Override
            public void onInteract(Player player, ClickType action, ItemStack item) {
                player.openInventory(getStatsChooseModeSG(player));
            }
        }, InventoryMenuBuilder.ALL_CLICK_TYPES);*/

        ItemStack skywarsStats = new ItemBuilder(Material.FIREBALL, 1).buildMeta().withDisplayName("§aSkywars").withLore("§7İstatistiklerini görmek için tıkla.").item().build();
        menu.withItem(31, skywarsStats, new ItemListener() {
            @Override
            public void onInteract(Player player, ClickType action, ItemStack item) {
                player.openInventory(getStatsChooseModeSkywars(player));
            }
        }, InventoryMenuBuilder.ALL_CLICK_TYPES);

        ItemStack backToMainMenuItem = new ItemBuilder(Material.BARRIER, 1).buildMeta().withDisplayName("§aProfile Geri Dön").withLore("§7Profil menüsüne dönmek için tıkla.").item().build();
        menu.withItem(40, backToMainMenuItem, new ItemListener() {
            @Override
            public void onInteract(Player player, ClickType action, ItemStack item) {
                player.openInventory(getProfile(player));
            }
        }, InventoryMenuBuilder.ALL_CLICK_TYPES);

        return menu.build();
    }

    private Inventory getStatsChooseModeSkywars(Player p) {
        InventoryMenuBuilder menu = new InventoryMenuBuilder(45, "§1İstatistikler");
        ItemStack skull = Utils.getSkull(p.getName(), "§aHesap Bilgileri");
        ItemStack emptyItem = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 1).buildMeta().withDisplayName("§a").item().build();

        menu.withItem(4, skull);
        for (int i = 9; i < 18; i++) {
            menu.withItem(i, emptyItem);
        }

        ItemStack soloStats = new ItemBuilder(Material.PAPER, 1).buildMeta().withDisplayName("§aSolo").withLore("§7Solo istatistiklerini görmek için tıkla.").item().build();
        menu.withItem(30, soloStats, new ItemListener() {
            @Override
            public void onInteract(Player player, ClickType action, ItemStack item) {
                player.openInventory(getStatsInventorySkywars(player.getName(), "solo"));
            }
        }, InventoryMenuBuilder.ALL_CLICK_TYPES);

        ItemStack duoStats = new ItemBuilder(Material.PAPER, 1).buildMeta().withDisplayName("§aDuo").withLore("§7Duo istatistiklerini görmek için tıkla.").item().build();
        menu.withItem(32, duoStats, new ItemListener() {
            @Override
            public void onInteract(Player player, ClickType action, ItemStack item) {
                player.openInventory(getStatsInventorySkywars(player.getName(), "duo"));
            }
        }, InventoryMenuBuilder.ALL_CLICK_TYPES);

        ItemStack backToMainMenuItem = new ItemBuilder(Material.BARRIER, 1).buildMeta().withDisplayName("§aİstatistik Menüsüne Dön").withLore("§7İstatistik menüsüne dönmek için tıkla.").item().build();
        menu.withItem(40, backToMainMenuItem, new ItemListener() {
            @Override
            public void onInteract(Player player, ClickType action, ItemStack item) {
                player.openInventory(getStats(player));
            }
        }, InventoryMenuBuilder.ALL_CLICK_TYPES);

        return menu.build();
    }

    private Inventory getStatsInventorySkywars(String playerName, String gameMode) {
        InventoryMenuBuilder menu = new InventoryMenuBuilder(45, "§1İstatistikler > " + playerName);

        ItemStack skull = Utils.getSkull(playerName, "§aHesap Bilgileri");
        ItemStack emptyItem = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 1).buildMeta().withDisplayName("§a").item().build();

        menu.withItem(4, skull);
        for (int i = 9; i < 18; i++) {
            menu.withItem(i, emptyItem);
        }

        int kills = 0;
        int deaths = 0;
        int playedGames = 0;
        int wins = 0;
        String playTime = "";
        int projectilesHit = 0;
        int projectilesLaunched = 0;
        int blocksPlaced = 0;
        int blocksBroken = 0;
        int itemsEnchanted = 0;
        int itemsCrafted = 0;
        int fishesCaught = 0;

        kills = plugin.getManager().sqlManager.getPlayerStatSkywars(playerName, "kills", gameMode);
        deaths = plugin.getManager().sqlManager.getPlayerStatSkywars(playerName, "deaths", gameMode);
        playedGames = plugin.getManager().sqlManager.getPlayerStatSkywars(playerName, "playedGames", gameMode);
        wins = plugin.getManager().sqlManager.getPlayerStatSkywars(playerName, "wins", gameMode);
        double playTimeD = Utils.round(plugin.getManager().sqlManager.getPlayerStatSkywars(playerName, "playTime", gameMode) / 3600);
        playTime = playTimeD + " saat";
        projectilesHit = plugin.getManager().sqlManager.getPlayerStatSkywars(playerName, "projectilesHit", gameMode);
        projectilesLaunched = plugin.getManager().sqlManager.getPlayerStatSkywars(playerName, "projectilesLaunched", gameMode);
        blocksPlaced = plugin.getManager().sqlManager.getPlayerStatSkywars(playerName, "blocksPlaced", gameMode);
        blocksBroken = plugin.getManager().sqlManager.getPlayerStatSkywars(playerName, "blocksBroken", gameMode);
        itemsEnchanted = plugin.getManager().sqlManager.getPlayerStatSkywars(playerName, "itemsEnchanted", gameMode);
        itemsCrafted = plugin.getManager().sqlManager.getPlayerStatSkywars(playerName, "itemsCrafted", gameMode);
        fishesCaught = plugin.getManager().sqlManager.getPlayerStatSkywars(playerName, "fishesCaught", gameMode);
        double kdr = new BigDecimal(deaths > 1 ? Double.valueOf(kills) / deaths : kills).setScale(2, RoundingMode.HALF_UP).doubleValue();
        int accuracy = (int) (projectilesLaunched > 0 ? (projectilesHit / Double.valueOf(projectilesLaunched)) * 100 : 0);

        ItemStackBuilder builder = new ItemStackBuilder(Material.PAPER);
        menu.withItem(19, builder.setName(ChatColor.GREEN + "Öldürme:").addLore(ChatColor.YELLOW + String.valueOf(kills)).build());
        menu.withItem(20, builder.setName(ChatColor.GREEN + "Ölme:").addLore(ChatColor.YELLOW + String.valueOf(deaths)).build());
        menu.withItem(21, builder.setName(ChatColor.GREEN + "KDR:").addLore("§7(Öldürme/Ölme Oranı)").addLore(ChatColor.YELLOW + String.valueOf(kdr)).build());
        menu.withItem(22, builder.setName(ChatColor.GREEN + "Oynanan Oyun:").addLore(ChatColor.YELLOW + String.valueOf(playedGames)).build());
        menu.withItem(23, builder.setName(ChatColor.GREEN + "Kazanma:").addLore(ChatColor.YELLOW + String.valueOf(wins)).build());
        menu.withItem(24, builder.setName(ChatColor.GREEN + "Oynama Süresi:").addLore(ChatColor.YELLOW + playTime).build());
        menu.withItem(25, builder.setName(ChatColor.GREEN + "İsabetli Ok Sayısı:").addLore(ChatColor.YELLOW + String.valueOf(projectilesHit)).build());
        menu.withItem(28, builder.setName(ChatColor.GREEN + "Atılan Ok Sayısı:").addLore(ChatColor.YELLOW + String.valueOf(projectilesLaunched)).build());
        menu.withItem(29, builder.setName(ChatColor.GREEN + "İsabet Oranı:").addLore(ChatColor.YELLOW + "%" + String.valueOf(accuracy)).build());
        menu.withItem(30, builder.setName(ChatColor.GREEN + "Koyulan Blok:").addLore(ChatColor.YELLOW + String.valueOf(blocksPlaced)).build());
        menu.withItem(31, builder.setName(ChatColor.GREEN + "Kırılan Blok:").addLore(ChatColor.YELLOW + String.valueOf(blocksBroken)).build());
        menu.withItem(32, builder.setName(ChatColor.GREEN + "Büyülenen Eşya:").addLore(ChatColor.YELLOW + String.valueOf(itemsEnchanted)).build());
        menu.withItem(33, builder.setName(ChatColor.GREEN + "Oluşturulan Eşya:").addLore(ChatColor.YELLOW + String.valueOf(itemsCrafted)).build());
        menu.withItem(34, builder.setName(ChatColor.GREEN + "Yakalanan Balık:").addLore(ChatColor.YELLOW + String.valueOf(fishesCaught)).build());

        ItemStack backToMainMenuItem = new ItemBuilder(Material.BARRIER, 1).buildMeta().withDisplayName("§aİstatistik Menüsüne Dön").withLore("§7İstatistik menüsüne dönmek için tıkla.").item().build();
        menu.withItem(40, backToMainMenuItem, new ItemListener() {
            @Override
            public void onInteract(Player player, ClickType action, ItemStack item) {
                player.openInventory(getStats(player));
            }
        }, InventoryMenuBuilder.ALL_CLICK_TYPES);

        return menu.build();
    }

    private Inventory getStatsChooseModeSG(Player p) {
        InventoryMenuBuilder menu = new InventoryMenuBuilder(45, "§1İstatistikler");
        ItemStack skull = Utils.getSkull(p.getName(), "§aHesap Bilgileri");
        ItemStack emptyItem = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 1).buildMeta().withDisplayName("§a").item().build();

        menu.withItem(4, skull);
        for (int i = 9; i < 18; i++) {
            menu.withItem(i, emptyItem);
        }

        ItemStack soloStats = new ItemBuilder(Material.PAPER, 1).buildMeta().withDisplayName("§aSolo").withLore("§7Solo istatistiklerini görmek için tıkla.").item().build();
        menu.withItem(30, soloStats, new ItemListener() {
            @Override
            public void onInteract(Player player, ClickType action, ItemStack item) {
                player.openInventory(getStatsInventorySG(player.getName(), "solo"));
            }
        }, InventoryMenuBuilder.ALL_CLICK_TYPES);

        ItemStack duoStats = new ItemBuilder(Material.PAPER, 1).buildMeta().withDisplayName("§aTeam").withLore("§7Team istatistiklerini görmek için tıkla.").item().build();
        menu.withItem(32, duoStats, new ItemListener() {
            @Override
            public void onInteract(Player player, ClickType action, ItemStack item) {
                player.openInventory(getStatsInventorySG(player.getName(), "team"));
            }
        }, InventoryMenuBuilder.ALL_CLICK_TYPES);

        ItemStack backToMainMenuItem = new ItemBuilder(Material.BARRIER, 1).buildMeta().withDisplayName("§aİstatistik Menüsüne Dön").withLore("§7İstatistik menüsüne dönmek için tıkla.").item().build();
        menu.withItem(40, backToMainMenuItem, new ItemListener() {
            @Override
            public void onInteract(Player player, ClickType action, ItemStack item) {
                player.openInventory(getStats(player));
            }
        }, InventoryMenuBuilder.ALL_CLICK_TYPES);

        return menu.build();
    }

    private Inventory getStatsInventorySG(String playerName, String gameMode) {
        InventoryMenuBuilder menu = new InventoryMenuBuilder(45, "§1İstatistikler > " + playerName);

        ItemStack skull = Utils.getSkull(playerName, "§aHesap Bilgileri");
        ItemStack emptyItem = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 1).buildMeta().withDisplayName("§a").item().build();

        menu.withItem(4, skull);
        for (int i = 9; i < 18; i++) {
            menu.withItem(i, emptyItem);
        }

        int kills = 0;
        int deaths = 0;
        int wins = 0;
        int games = 0;
        int chests_opened = 0;
        int points = 0;

        kills = plugin.getManager().sqlManager.getPlayerStatSG(playerName, "kills", gameMode);
        deaths = plugin.getManager().sqlManager.getPlayerStatSG(playerName, "deaths", gameMode);
        games = plugin.getManager().sqlManager.getPlayerStatSG(playerName, "games", gameMode);
        wins = plugin.getManager().sqlManager.getPlayerStatSG(playerName, "wins", gameMode);
        chests_opened = plugin.getManager().sqlManager.getPlayerStatSG(playerName, "chests_opened", gameMode);
        points = plugin.getManager().sqlManager.getPlayerStatSG(playerName, "points", gameMode);
        double kdr = new BigDecimal(deaths > 1 ? Double.valueOf(kills) / deaths : kills).setScale(2, RoundingMode.HALF_UP).doubleValue();

        ItemStackBuilder builder = new ItemStackBuilder(Material.PAPER);
        menu.withItem(19, builder.setName(ChatColor.GREEN + "Kazanma:").addLore(ChatColor.YELLOW + String.valueOf(wins)).build());
        menu.withItem(20, builder.setName(ChatColor.GREEN + "Öldürme:").addLore(ChatColor.YELLOW + String.valueOf(kills)).build());
        menu.withItem(21, builder.setName(ChatColor.GREEN + "Ölme:").addLore(ChatColor.YELLOW + String.valueOf(deaths)).build());
        menu.withItem(22, builder.setName(ChatColor.GREEN + "KDR:").addLore("§7(Öldürme/Ölme Oranı)").addLore(ChatColor.YELLOW + String.valueOf(kdr)).build());
        menu.withItem(23, builder.setName(ChatColor.GREEN + "Oynanan Oyun:").addLore(ChatColor.YELLOW + String.valueOf(games)).build());
        menu.withItem(24, builder.setName(ChatColor.GREEN + "Açılan Sandık:").addLore(ChatColor.YELLOW + String.valueOf(chests_opened)).build());
        menu.withItem(25, builder.setName(ChatColor.GREEN + "Puan:").addLore(ChatColor.YELLOW + String.valueOf(points)).build());

        ItemStack backToMainMenuItem = new ItemBuilder(Material.BARRIER, 1).buildMeta().withDisplayName("§aİstatistik Menüsüne Dön").withLore("§7İstatistik menüsüne dönmek için tıkla.").item().build();
        menu.withItem(40, backToMainMenuItem, new ItemListener() {
            @Override
            public void onInteract(Player player, ClickType action, ItemStack item) {
                player.openInventory(getStats(player));
            }
        }, InventoryMenuBuilder.ALL_CLICK_TYPES);

        return menu.build();
    }
}
