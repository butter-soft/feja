package io.buttersoft.feja.ranging

import arrow.core.Either
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class DecideRangingExecutionStartTest : StringSpec({
    "시작 전 실행 객체는 시작할 수 있다" {
        decideRangingExecutionStart(
            BeaconRangingExecutionPhase.NOT_STARTED
        ) shouldBe Either.Right(BeaconRangingStartDecision.START)
    }

    "실행 중인 객체의 시작 요청은 기존 실행을 유지한다" {
        decideRangingExecutionStart(
            BeaconRangingExecutionPhase.RUNNING
        ) shouldBe Either.Right(BeaconRangingStartDecision.KEEP_RUNNING)
    }

    "종료된 실행 객체는 다시 시작할 수 없다" {
        decideRangingExecutionStart(
            BeaconRangingExecutionPhase.TERMINATED
        ) shouldBe Either.Left(BeaconRangingStartError.ExecutionTerminated)
    }
})