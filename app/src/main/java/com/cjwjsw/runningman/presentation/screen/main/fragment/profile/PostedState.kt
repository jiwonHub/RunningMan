package com.cjwjsw.runningman.presentation.screen.main.fragment.profile

sealed class PostedState{

    object beforePosted : PostedState() // 포스팅 전 상태

    object Success : PostedState() // 성공

    object Loading : PostedState() // 로딩중

    object Failure : PostedState() // 실패

}