package com.nftworlds.shop.shop.item.types;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

@Getter
@Setter
public class SpawnerItem extends Item {

    private EntityType entityType;

    public SpawnerItem(String id, Material material) {
        super(id, material);
    }

    @Override
    public void applyExtra(ItemMeta itemMeta) {
        BlockStateMeta bsm = (BlockStateMeta) itemMeta;
        CreatureSpawner cs = (CreatureSpawner) bsm.getBlockState();
        cs.setSpawnedType(entityType);
        bsm.setBlockState(cs);
    }

}
