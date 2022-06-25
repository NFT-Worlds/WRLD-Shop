package com.nftworlds.shop.payload;

import com.nftworlds.shop.shop.item.types.Item;
import lombok.Getter;

public record PlayerBuyItemPayload(@Getter Item shopItem, @Getter int amount) {
}
