package com.cjwjsw.runningman.presentation.screen.main.fragment.main.water

import com.cjwjsw.runningman.domain.model.WaterModel

class WaterCaretaker {
    private val mementoList = mutableListOf<WaterModel>()

    // 메멘토 추가
    fun addMemento(memento: WaterModel) {
        mementoList.add(memento)
    }

    // 마지막 메멘토 제거
    fun removeLastMemento() {
        if (mementoList.isNotEmpty()) {
            mementoList.removeLast()
        }
    }

    // 마지막 메멘토 가져오기
    fun getLastMemento(): WaterModel? {
        return mementoList.lastOrNull()
    }
}