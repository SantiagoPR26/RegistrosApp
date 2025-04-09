package com.allcashless.registros.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.allcashless.registros.databinding.ItemEventoBinding
import com.allcashless.registros.models.Evento
import com.allcashless.registros.util.toDateFormat
import com.bumptech.glide.Glide
import kotlin.apply
import kotlin.collections.firstOrNull

class EventosAdapter ( private val onItemClick: (Evento) -> Unit
) : ListAdapter<Evento, EventosAdapter.EventoViewHolder>(EventoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventoViewHolder {
        val binding = ItemEventoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventoViewHolder, position: Int) {
        val evento = getItem(position)
        holder.bind(evento)
    }

    inner class EventoViewHolder(private val binding: ItemEventoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(evento: Evento) {
            binding.apply {
                eventNameText.text = evento.nombre
                dateText.text = evento.fecha.toDateFormat() // Asegúrate de que esta función exista
                cityText.text = evento.ciudad?.nombre ?: "N/A"
                placeText.text = evento.lugar ?: ""

                // Filtramos la imagen con tipo == 1 (primera coincidencia)
                val imagenPrincipal = evento.imagenes.firstOrNull { it.tipo == 3 }

                // Cargamos la imagen con Glide (si existe)
                Glide.with(root.context)
                    .load(imagenPrincipal?.url)
                    .centerCrop() // Ajusta la imagen al ImageView
                    .into(eventImage)

                root.setOnClickListener {
                    onItemClick(evento)
                }
            }
        }
    }
}

class EventoDiffCallback : DiffUtil.ItemCallback<Evento>() {
    override fun areItemsTheSame(oldItem: Evento, newItem: Evento): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Evento, newItem: Evento): Boolean {
        return oldItem == newItem
    }
}