package com.ancientones.squadup

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment


class AlertDialogFragment : DialogFragment() {

    var dialogType: String = ""
    lateinit var thisContext: Context

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        thisContext = requireContext()
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(thisContext)

        if (savedInstanceState != null) {
            dialogType = savedInstanceState.getString("DIALOG_TYPE").toString()
        }

        if (dialogType == "Join") {
            alertDialogBuilder.setTitle("Make your way there")
            alertDialogBuilder.setMessage("When you reach, we will check you in automatically")
            alertDialogBuilder.setPositiveButton(
                ("Got it"),
                DialogInterface.OnClickListener { dialogInterface, i ->
                    dismiss()
                    requireActivity().finish()
                })
            alertDialogBuilder.setOnDismissListener {  }


        } else if (dialogType == "Automatic") {
            alertDialogBuilder.setTitle("Have you arrived?")
            alertDialogBuilder.setMessage("Based on your location, you are very close to the drop in and ready to check-in to the Drop In session")

            alertDialogBuilder.setPositiveButton(
                ("I'm here"),
                DialogInterface.OnClickListener { dialogInterface, i ->
                    dismiss()
                    //do check in
                })
            alertDialogBuilder.setNegativeButton(
                ("Not yet"),
                DialogInterface.OnClickListener { dialogInterface, i ->
                    dismiss()
                    //cooldown before pops up again?
                }
            )
        }

        return alertDialogBuilder.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (container != null) {
            thisContext = container.context
        }
        this.dialog?.setCanceledOnTouchOutside(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)

        savedInstanceState.putString("DIALOG_TYPE", dialogType)

    }

    override fun onCancel(dialog: DialogInterface) {
        dismiss()
        requireActivity().finish()
        super.onCancel(dialog)
    }

}



