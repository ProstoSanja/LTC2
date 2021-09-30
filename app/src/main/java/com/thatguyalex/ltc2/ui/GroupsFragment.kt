package com.thatguyalex.ltc2.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.thatguyalex.ltc2.ui.adapters.GroupsAdapter
import com.thatguyalex.ltc2.R
import com.thatguyalex.ltc2.viewmodel.GroupsState
import com.thatguyalex.ltc2.viewmodel.MainViewModel

class GroupsFragment  : Fragment(R.layout.fragment_groups) {

    private val model: MainViewModel by activityViewModels()

    private lateinit var groupsRecycler: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        groupsRecycler = view.findViewById(R.id.groups_recycler_view)
        groupsRecycler.layoutManager = LinearLayoutManager(context)
        groupsRecycler.adapter = GroupsAdapter(emptyList(), this::groupSelected)

        model.groupsList.observe(viewLifecycleOwner) { list ->
            (groupsRecycler.adapter as GroupsAdapter).updateDataSet(list)
        }

        model.socsApiManager.fetchGoups()
    }

    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar).title = "LTC2"
    }

    private fun groupSelected(groupsState: GroupsState) {
        model.selectedGroup.value = groupsState
        requireActivity().supportFragmentManager.commit {
            setReorderingAllowed(true)
            addToBackStack(null)
            replace<StudentsFragment>(R.id.fragment_container_view)
        }
    }
}