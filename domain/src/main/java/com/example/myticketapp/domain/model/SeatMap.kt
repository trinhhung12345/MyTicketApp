package com.example.myticketapp.domain.model

data class SeatMap(
    val id: Int,
    val viewboxWidth: Float,
    val viewboxHeight: Float,
    val sections: List<Section>
)

data class Section(
    val id: Int,
    val name: String,
    val isStage: Boolean,
    val ticketTypeId: Int?,
    val maxQtyPerOrder: Int,
    val attribute: SectionAttribute,
    val seats: List<Seat>
)

data class SectionAttribute(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val color: Int
)

data class Seat(
    val id: Int,
    val code: String,
    val rowIndex: Int,
    val colIndex: Int,
    val status: String,
    val isSalable: Boolean,
    val price: Long
)