package com.example.progrivazucchi

interface OnItemClickListener {
    // per capire quale elemento nella recyclerView è stato premuto
    fun onItemClickListener(position : Int)
    fun onItemLongClickListener(position : Int)
}