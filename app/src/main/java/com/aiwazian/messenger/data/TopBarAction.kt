package com.aiwazian.messenger.data

import androidx.compose.ui.graphics.vector.ImageVector

data class TopBarAction(
    val icon: ImageVector,
    val onClick: (() -> Unit)? = null,
    val dropdownActions: Array<DropdownMenuAction> = emptyArray()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        
        other as TopBarAction
        
        if (icon != other.icon) return false
        if (onClick != other.onClick) return false
        if (!dropdownActions.contentEquals(other.dropdownActions)) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        var result = icon.hashCode()
        result = 31 * result + onClick.hashCode()
        result = 31 * result + dropdownActions.contentHashCode()
        return result
    }
}
