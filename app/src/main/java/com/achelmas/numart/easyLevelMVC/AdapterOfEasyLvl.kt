package com.achelmas.numart.easyLevelMVC

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.achelmas.numart.GameActivity
import com.achelmas.numart.R

class AdapterOfEasyLvl(var activity: Activity, var easyLvlList: ArrayList<ModelOfEasyLvl>) : RecyclerView.Adapter<AdapterOfEasyLvl.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var view: View = LayoutInflater.from(activity).inflate(R.layout.levels_card_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = easyLvlList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model: ModelOfEasyLvl = easyLvlList.get(position)
        holder.targetOfTitle.text = "${model.targetNumber}. ${activity.resources.getString(R.string.our_target)} ${model.target}"

        // Always set click listener, check unlock status inside
        holder.levelButton.setOnClickListener {
            if (model.isUnlocked) {
                val intent = Intent(activity, GameActivity::class.java).apply {
                    putExtra("Target", model.target)
                    putExtra("Target Number", model.targetNumber)
                    putExtra("Number1", model.number1)
                    putExtra("Number2", model.number2)
                    putExtra("Number3", model.number3)
                    putExtra("Number4", model.number4)
                }
                activity.startActivity(intent)
            } else {
                // Show Toast for locked level
                Toast.makeText(
                    activity,
                    activity.getString(R.string.complete_previous_target, model.targetNumber.toInt() - 1),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Visual state management
        if (model.isUnlocked) {
            holder.levelButton.alpha = 1.0f
            holder.levelButton.isEnabled = true
        } else {
            holder.levelButton.alpha = 0.5f
            holder.levelButton.isEnabled = true
        }
    }

    inner class MyViewHolder(i: View) : RecyclerView.ViewHolder(i) {
        var targetOfTitle: TextView
        var levelButton: CardView

        init {
            targetOfTitle = i.findViewById(R.id.levelsCardItem_targetOfTitleId)
            levelButton = i.findViewById(R.id.levelsCardItem_buttonId)
        }

    }
}