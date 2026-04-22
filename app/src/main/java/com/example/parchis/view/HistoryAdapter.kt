package com.example.parchis.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.parchis.databinding.ItemPartidaBinding
import com.example.parchis.model.Partida
import com.example.parchis.model.ResultadoPartida
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Función de extensión para formatear la fecha (Punto 8 del temario)
fun Date.toFormattedString(): String {
    return SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(this)
}

class HistoryAdapter(private val partidas: List<Partida>) : 
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemPartidaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPartidaBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val partida = partidas[position]
        val context = holder.itemView.context

        with(holder.binding) {
            val resultadoStr = when(partida.resultado) {
                ResultadoPartida.VICTORIA -> "🏆 VICTORIA"
                ResultadoPartida.DERROTA -> "❌ DERROTA"
                ResultadoPartida.ABANDONADA -> "🏳️ ABANDONADA"
            }

            tvItemResultado.text = resultadoStr
            tvItemFecha.text = partida.fecha.toFormattedString() // Uso de la extensión
            tvItemDetalles.text = "Jugadores: ${partida.jugadores.joinToString(", ")}"

            val color = if(partida.resultado == ResultadoPartida.VICTORIA)
                android.R.color.holo_green_dark
            else android.R.color.holo_red_dark
            
            tvItemResultado.setTextColor(ContextCompat.getColor(context, color))
        }
    }

    override fun getItemCount() = partidas.size
}
