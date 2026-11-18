package io.buttersoft.feja.ranging

import arrow.core.Either
import io.buttersoft.feja.beacon.BeaconTarget
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.util.UUID

class DecideBeaconRangingStartTest : StringSpec({
    val uuid = UUID.fromString("706E7568-5F70-656F-706C-655F74610032")
    val target = BeaconTarget.Uuid(uuid)
    val executionId = BeaconRangingExecutionId(
        UUID.fromString("00000000-0000-4000-8000-000000000001")
    )

    "중단 상태에서는 새 실행을 시작한다" {
        decideBeaconRangingStart(
            BeaconRangingState.Stopped,
            target,
        ) shouldBe Either.Right(BeaconRangingStartDecision.START)
    }

    "값이 같은 대상을 요청하면 기존 실행을 유지한다" {
        val state = BeaconRangingState.Running(target, executionId)
        val sameTarget = BeaconTarget.Uuid(UUID.fromString(uuid.toString()))

        decideBeaconRangingStart(state, sameTarget) shouldBe
                Either.Right(BeaconRangingStartDecision.KEEP_RUNNING)
    }

    "다른 대상을 요청하면 현재 대상과 요청 대상을 담은 충돌을 반환한다" {
        val state = BeaconRangingState.Running(target, executionId)
        val requested = BeaconTarget.UuidMajor(uuid, 0u)

        decideBeaconRangingStart(state, requested) shouldBe Either.Left(
            BeaconRangingStartError.TargetConflict(target, requested)
        )
    }
})