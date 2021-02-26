package de.rki.coronawarnapp.datadonation.analytics.modules.keysubmission

import de.rki.coronawarnapp.risk.RiskLevelSettings
import de.rki.coronawarnapp.util.TimeStamper
import org.joda.time.Duration
import org.joda.time.Instant
import javax.inject.Inject
import kotlin.math.max

class AnalyticsKeySubmissionRepository @Inject constructor(
    private val storage: AnalyticsKeySubmissionStorage,
    private val timeStamper: TimeStamper,
    private val riskLevelSettings: RiskLevelSettings
) {
    val testResultReceivedAt: Long
        get() = storage.testResultReceivedAt.value

    private val testRegisteredAt: Long
        get() = storage.testRegisteredAt.value

    val submitted: Boolean
        get() = storage.submitted.value

    val submittedInBackground: Boolean
        get() = submitted && storage.submittedInBackground.value

    val submittedAfterCancel: Boolean
        get() = submitted && storage.submittedAfterCancel.value

    val submittedAfterSymptomFlow: Boolean
        get() = storage.submittedAfterSymptomFlow.value

    val submittedWithTeleTAN: Boolean
        get() = submitted && storage.registeredWithTeleTAN.value

    val lastSubmissionFlowScreen: Int
        get() = storage.lastSubmissionFlowScreen.value

    val advancedConsentGiven: Boolean
        get() = submitted && storage.advancedConsentGiven.value

    val hoursSinceTestResult: Int
        get() = Duration.millis(max(timeStamper.nowUTC.millis - testResultReceivedAt, 0)).toStandardHours().hours

    val hoursSinceTestRegistration: Int
        get() = Duration.millis(max(timeStamper.nowUTC.millis - testRegisteredAt, 0L)).toStandardHours().hours

    val daysSinceMostRecentDateAtRiskLevelAtTestRegistration: Int
        get() = Duration(
            riskLevelSettings.lastChangeCheckedRiskLevelTimestamp,
            Instant.ofEpochMilli(testRegisteredAt)
        ).standardDays.toInt()

    val hoursSinceHighRiskWarningAtTestRegistration: Int
        get() = storage.hoursSinceHighRiskWarningAtTestRegistration.value

    fun reset() = storage.clear()
}
