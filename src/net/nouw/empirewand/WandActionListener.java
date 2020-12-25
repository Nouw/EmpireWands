package net.nouw.empirewand;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class WandActionListener implements Listener {
    private final Plugin plugin;
    private Spell selectedSpell = Spell.THUNDER;

    public WandActionListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void fire(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        if ((action == Action.PHYSICAL) || (event.getItem() == null) || (event.getItem().getType() == Material.BLAZE_ROD) && (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK)) {
            Location target;

            if (event.getClickedBlock() == null) {
                target = player.getLineOfSight(null, 200).get(player.getLineOfSight(null, 20).size() - 1).getLocation();
            } else {
                target = event.getClickedBlock().getLocation();
            }

            switch (selectedSpell) {
                case THUNDER:
                    thunder(target);
                    break;
                case LAUNCH:
                    launch(target);
                    break;
                case NUKE:
                    nuke(target);
                    break;
                case FIREBALL:
                    fireBall(player);
                    break;
                case FREEZE:
                    freeze(target);
                    break;
            }
        } else if ((event.getItem() == null) || (event.getItem().getType() == Material.BLAZE_ROD) && (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR)) {
            switch (selectedSpell) {
                case THUNDER:
                    this.selectedSpell = Spell.LAUNCH;
                    changeWandName(Objects.requireNonNull(event.getItem()), "Launch");
                    break;
                case LAUNCH:
                    this.selectedSpell = Spell.NUKE;
                    changeWandName(Objects.requireNonNull(event.getItem()), "Nuke");
                    break;
                case NUKE:
                    this.selectedSpell = Spell.FIREBALL;
                    changeWandName(Objects.requireNonNull(event.getItem()), "Fireball");
                    break;
                case FIREBALL:
                    this.selectedSpell = Spell.FREEZE;
                    changeWandName(Objects.requireNonNull(event.getItem()), "Freeze");
                    break;
                case FREEZE:
                    this.selectedSpell = Spell.THUNDER;
                    changeWandName(Objects.requireNonNull(event.getItem()), "Thunder");
                    break;
            }
        }
    }

    public void changeWandName(ItemStack item, String name) {
        ItemMeta meta = item.getItemMeta();
        Objects.requireNonNull(meta).setDisplayName(ChatColor.RED + "Empire wand" + ChatColor.GRAY + "(" + name + ")");

        item.setItemMeta(meta);
    }

    public void createFirework(Location target, Color color, FireworkEffect.Type ball) {
        Firework firework = (Firework) Objects.requireNonNull(target.getWorld()).spawnEntity(target, EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();

        fireworkMeta.setPower(3);
        fireworkMeta.addEffect(FireworkEffect.builder()
                .withColor(color)
                .withColor(Color.BLACK)
                .with(ball)
                .flicker(true)
                .build());

        firework.setFireworkMeta(fireworkMeta);

        firework.detonate();
    }

    public void thunder(Location target) {
        Objects.requireNonNull(target.getWorld()).strikeLightning(target);

        createFirework(target, Color.AQUA, FireworkEffect.Type.BALL_LARGE);
    }

    public void launch(Location target) {
        Collection<Entity> entities = Objects.requireNonNull(target.getWorld()).getNearbyEntities(target, 5, 5, 5);

        for (Entity entity : entities) {
            System.out.println(entity.toString());
            entity.setFireTicks(10 * 20);
            entity.setVelocity(new Vector(0, 2, 0));
        }

        createFirework(target, Color.PURPLE, FireworkEffect.Type.BALL_LARGE);
    }

    public void nuke(Location target) {
        Collection<Entity> entities = Objects.requireNonNull(target.getWorld()).getNearbyEntities(target, 5, target.getY(), 5);

        for (Entity entity : entities) {
            Entity arrow = target.getWorld().spawnArrow(new Location(entity.getWorld(), target.getX(), target.getY() + 20, target.getZ()),
                    new Vector(0, -1, 0), (float) 0.6, 0);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (arrow.getLocation().getY() < target.getY() + 2) {
                        target.getWorld().createExplosion(arrow.getLocation(), 3);
                        createFirework(arrow.getLocation(), Color.RED, FireworkEffect.Type.BALL_LARGE);
                        cancel();
                    } else {
                        createFirework(arrow.getLocation(), Color.ORANGE, FireworkEffect.Type.BALL_LARGE);
                    }
                }
            }.runTaskTimer(plugin, 0L, 2L);
        }
    }

    public void fireBall(Player player) {
        Entity ball = Objects.requireNonNull(player.getWorld()).spawnEntity(player.getEyeLocation(), EntityType.FIREBALL);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (ball.isDead()) {
                    player.getWorld().createExplosion(ball.getLocation(), 5);
                    cancel();
                } else {
                    createFirework(ball.getLocation(), Color.RED, FireworkEffect.Type.BALL);
                }
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    public void freeze(Location target) {

        createFirework(target, Color.BLUE, FireworkEffect.Type.BALL_LARGE);

        Collection<Entity> entities = Objects.requireNonNull(target.getWorld()).getNearbyEntities(target, 1, target.getY(), 1);

        for (Entity entity : entities) {
            List<Location> sphereCords = Sphere.generateSphere(entity.getLocation(), 3, false);

            for (Location cord : sphereCords) {
                cord.getBlock().setType(Material.ICE);
            }
        }


    }
}
