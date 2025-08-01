package com.example.studytimerapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer // Import CountDownTimer for countdown logic
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas // Import Canvas for strikethrough AND Pie Chart
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items // Ensure this import is present for grids
import androidx.compose.foundation.lazy.items // Keep this for TaskListScreen and SubjectDetailScreen
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
// Import new icons needed from MainActivity.kt (original)
import androidx.compose.material.icons.filled.Forum // For Forum
import androidx.compose.material.icons.filled.Quiz // For Practice Questions
import androidx.compose.material.icons.filled.Lightbulb // For Insights
import androidx.compose.material.icons.filled.Repeat // For Habit (using Repeat as an example)
// Import icons for habits from MainActivity.kt (original)
import androidx.compose.material.icons.filled.WbSunny // Used for Habit and potentially Today's Stat
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Hotel // For Bedding
import androidx.compose.material.icons.filled.Kitchen // For Expired Food
import androidx.compose.material.icons.filled.Bathtub // For Bathroom
// Import icons needed from MainActivity1.kt (for Study Plan section)
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Book // Already present, used for Diary
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AssuredWorkload
import androidx.compose.material.icons.filled.Balance
// Icons for Statistics Screen
import androidx.compose.material.icons.filled.LightMode // For Today
import androidx.compose.material.icons.filled.DateRange // For This Week
import androidx.compose.material.icons.filled.CalendarToday // For This Month (can also be CalendarMonth)
import androidx.compose.material.icons.filled.EventNote // For This Year
import androidx.compose.material.icons.filled.CheckCircle // For Total
import androidx.compose.material.icons.filled.PieChart // NEW: Icon for Pie Chart details (optional)
// NEW: Icons for Recitation (from V2.1)
import androidx.compose.material.icons.filled.Psychology // For Recall
import androidx.compose.material.icons.filled.School // For Learning Mode
import androidx.compose.material.icons.filled.Sync // For Review Mode
import androidx.compose.material.icons.filled.MergeType // For Hybrid Mode
import androidx.compose.material.icons.filled.CreateNewFolder // Icon for "Add Chapter"
// NEW: Icons for Q&A Feature (from MainActivity.kt)
import androidx.compose.material.icons.filled.People // For Teacher Q&A
import androidx.compose.material.icons.filled.SmartToy // For AI Q&A

import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Security


import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset // Import Offset for Canvas
import androidx.compose.ui.geometry.Size // NEW: For Pie Chart
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke // NEW: For Pie Chart border
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource // Use painterResource for local drawables
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow // Import TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope // Import viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import kotlinx.coroutines.Job // Import Job
import kotlinx.coroutines.delay // Import delay
import kotlinx.coroutines.flow.MutableStateFlow // Use StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow // Use StateFlow
import kotlinx.coroutines.flow.asStateFlow // Use StateFlow
import kotlinx.coroutines.flow.map // Import map operator
import kotlinx.coroutines.flow.stateIn // Import stateIn operator
import kotlinx.coroutines.flow.update // Use StateFlow update
import kotlinx.coroutines.launch // Import launch
// Import older date/time classes compatible with API 24 (from MainActivity.kt)
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit // Import TimeUnit
// Add Typography if missing
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import kotlin.math.min
import kotlin.random.Random

// --- Data Models ---

// NEW: Enum for Time Intervals
enum class TimeInterval(val displayName: String) {
    MORNING("上午"),
    NOON("中午"),
    AFTERNOON("下午"),
    EVENING("晚上")
}

// Task related data models
data class StudyTask(
    val id: String,
    val title: String,
    val type: TaskType,
    val timeInterval: TimeInterval,
    val timingMode: TimingMode = TimingMode.FORWARD,
    val targetMinutes: Int = 0,
    var completedMinutes: Int = 0,
    val targetCount: Int = 1,
    var completedCount: Int = 0,
    var focusCount: Int = 0,
    val backgroundImageUrl: String? = null,
    val backgroundColor: Color = Color(0xFFE0E0E0),
    var isCompleted: Boolean = false,
    val creationTimestamp: Long = System.currentTimeMillis(),
    var completionTimestamp: Long? = null
)

enum class TaskType { TIMED_GOAL, HABIT }
enum class TimingMode { COUNTDOWN, FORWARD, NONE }

// Data models for Study Column
data class KnowledgePoint(val id: String, val text: String, val details: String = "")
data class Chapter(val id: String, val title: String, val knowledgePoints: List<KnowledgePoint>)
data class Subject(val id: String, val name: String, val icon: ImageVector, val chapters: List<Chapter>)

// Data model for Predefined Habit
data class PredefinedHabit(
    val name: String,
    val description: String? = null,
    val icon: ImageVector? = null
)

// Timer State
data class TimerState(
    val activeTaskId: String? = null,
    val taskTitle: String = "",
    val timingMode: TimingMode = TimingMode.FORWARD,
    val targetMillis: Long = 0,
    val elapsedTimeMillis: Long = 0,
    val remainingTimeMillis: Long = 0,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val pauseStartTimeMillis: Long = 0
)

// Data model for Statistics
data class TaskStatistics(
    val completedToday: Int = 0,
    val completedTodayTime: Int = 0,
    val completedThisWeek: Int = 0,
    val completedThisWeekTime: Int = 0,
    val completedThisMonth: Int = 0,
    val completedThisMonthTime: Int = 0,
    val completedThisYear: Int = 0,
    val completedThisYearTime: Int = 0,
    val completedTotal: Int = 0,
    val completedTotalTime: Int = 0
)

// Data model for Pie Chart Slice
data class PieChartSlice(
    val name: String,
    val value: Float,
    val color: Color
)

// Data Models for FSRS Recitation Feature (from V2.1)
enum class FamiliarityLevel(val levelName: String, val color: Color) {
    FORGOTTEN("忘记了", Color(0xFFD32F2F)),
    DIFFICULT("有点困难", Color(0xFFF57C00)),
    FAMILIAR("熟悉", Color(0xFF388E3C)),
    MASTERED("完美掌握", Color(0xFF1976D2))
}

data class RecitationState(
    val knowledgePointId: String,
    val familiarity: FamiliarityLevel = FamiliarityLevel.FORGOTTEN,
    val lastReviewedTimestamp: Long = 0L,
    val nextReviewTimestamp: Long = 0L,
    val reviewCount: Int = 0
)

// Data model for FSRS Practice Question Feature
data class PracticeQuestionState(
    val questionId: String,
    val familiarity: FamiliarityLevel = FamiliarityLevel.FORGOTTEN,
    val lastReviewedTimestamp: Long = 0L,
    val nextReviewTimestamp: Long = 0L,
    val reviewCount: Int = 0
)

// MERGED: Data Models for Q&A Feature (from MainActivity.kt)
data class ChatMessage(val id: String = UUID.randomUUID().toString(), val text: String, val isFromUser: Boolean)
data class TeacherQuestion(
    val id: String,
    val knowledgePointId: String,
    val studentQuestion: String,
    val authorName: String, // 提问者
    val timestamp: Long = System.currentTimeMillis(),
    val answers: List<Answer> = emptyList() // 回答列表
)

// --- Data Models for Q&A Feature ---
enum class AnswerStatus {
    PENDING,  // 待审核
    APPROVED, // 已批准
    REJECTED  // 已拒绝
}

data class Answer(
    val id: String = UUID.randomUUID().toString(),
    val questionId: String,
    val content: String,
    val authorName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: AnswerStatus = AnswerStatus.PENDING
)

// --- Data Models for User Profile ---
data class UserProfile(
    val id: String = "user123",
    val nickname: String = "学习者",
    val avatarUrl: String = "https://placehold.co/128x128/007AFF/FFFFFF?text=学",
    val studySignature: String = "天行健，君子以自强不息。"
)

// --- Data Models for Practice Questions ---
enum class QuestionType(val displayName: String) {
    MULTIPLE_CHOICE("选择题"),
    SHORT_ANSWER("简答题")
}

data class MultipleChoiceOption(val id: String = UUID.randomUUID().toString(), val text: String)

sealed class PracticeQuestion {
    abstract val id: String
    abstract val knowledgePointId: String
    abstract val questionText: String
    abstract val analysis: String
    abstract val type: QuestionType
}

data class MultipleChoiceQuestion(
    override val id: String = UUID.randomUUID().toString(),
    override val knowledgePointId: String,
    override val questionText: String,
    override val analysis: String,
    val options: List<MultipleChoiceOption>,
    val correctOptionId: String
) : PracticeQuestion() {
    override val type: QuestionType = QuestionType.MULTIPLE_CHOICE
}

data class ShortAnswerQuestion(
    override val id: String = UUID.randomUUID().toString(),
    override val knowledgePointId: String,
    override val questionText: String,
    override val analysis: String,
    val referenceAnswer: String
) : PracticeQuestion() {
    override val type: QuestionType = QuestionType.SHORT_ANSWER
}


// --- ViewModel ---
class TaskViewModel : ViewModel() {

    private val diaryDateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val monthYearFormatter = SimpleDateFormat("yyyy年 MMMM", Locale.CHINA)
    private val dayOfWeekFormatter = SimpleDateFormat("E", Locale.getDefault())
    private val insightTimeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    private val _tasks = MutableStateFlow(getSampleTasks())
    val sortedTasks: StateFlow<List<StudyTask>> = _tasks
        .map { tasks ->
            val (incomplete, completed) = tasks.partition { !it.isCompleted }
            val sortedIncomplete = incomplete.sortedWith(
                compareBy<StudyTask> { it.timeInterval }
                    .thenBy { it.creationTimestamp }
            )
            val sortedCompleted = completed.sortedByDescending { it.completionTimestamp }
            sortedIncomplete + sortedCompleted
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()
    private var timerJob: Job? = null
    private var countdownTimer: CountDownTimer? = null

    private val _subjects = MutableStateFlow(getSampleSubjects())
    val subjects: StateFlow<List<Subject>> = _subjects.asStateFlow()

    private val _experienceImageUrls = MutableStateFlow(getSampleExperienceImages())
    val experienceImageUrls: StateFlow<List<String>> = _experienceImageUrls.asStateFlow()

    private val _diaryEntries = MutableStateFlow<Map<String, String>>(emptyMap())
    val diaryEntries: StateFlow<Map<String, String>> = _diaryEntries.asStateFlow()

    private val _predefinedHabits = MutableStateFlow(getSampleHabits())
    val predefinedHabits: StateFlow<List<PredefinedHabit>> = _predefinedHabits.asStateFlow()

    private val _taskStatistics = MutableStateFlow(TaskStatistics())
    val taskStatistics: StateFlow<TaskStatistics> = _taskStatistics.asStateFlow()

    // State for FSRS Recitation (from V2.1)
    private val _recitationStates = MutableStateFlow<Map<String, RecitationState>>(emptyMap())
    val recitationStates: StateFlow<Map<String, RecitationState>> = _recitationStates.asStateFlow()

    // MERGED: State for Teacher Q&A Feature (from MainActivity.kt)
    private val _teacherQuestions = MutableStateFlow(getSampleTeacherQuestions())
    val teacherQuestions: StateFlow<List<TeacherQuestion>> = _teacherQuestions.asStateFlow()

    // NEW: State for Practice Questions
    private val _practiceQuestions = MutableStateFlow(getSamplePracticeQuestions())
    val practiceQuestions: StateFlow<List<PracticeQuestion>> = _practiceQuestions.asStateFlow()

    // NEW: State for FSRS Practice Question tracking
    private val _practiceQuestionStates = MutableStateFlow<Map<String, PracticeQuestionState>>(emptyMap())
    val practiceQuestionStates: StateFlow<Map<String, PracticeQuestionState>> = _practiceQuestionStates.asStateFlow()

    // --- 新增代码开始 ---
// NEW: State for User Profile
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // NEW: Functions to update profile (placeholders for now)
    fun updateUserNickname(newNickname: String) {
        _userProfile.update { it.copy(nickname = newNickname) }
    }

    fun updateUserSignature(newSignature: String) {
        _userProfile.update { it.copy(studySignature = newSignature) }
    }

    // For Edit Profile Dialog
    private val _isEditProfileDialogOpen = MutableStateFlow(false)
    val isEditProfileDialogOpen: StateFlow<Boolean> = _isEditProfileDialogOpen.asStateFlow()

    fun openEditProfileDialog() { _isEditProfileDialogOpen.value = true }
    fun closeEditProfileDialog() { _isEditProfileDialogOpen.value = false }

    // For Logout Dialog
    private val _isLogoutConfirmOpen = MutableStateFlow(false)
    val isLogoutConfirmOpen: StateFlow<Boolean> = _isLogoutConfirmOpen.asStateFlow()

    fun openLogoutConfirmDialog() { _isLogoutConfirmOpen.value = true }
    fun closeLogoutConfirmDialog() { _isLogoutConfirmOpen.value = false }

    fun logout() {
        // 在一个真实的app中，这里会清除用户令牌等。
        // 在当前应用中，我们只重置个人资料为默认值。
        _userProfile.value = UserProfile()
        closeLogoutConfirmDialog()
    }

    init {
        viewModelScope.launch {
            _tasks.collect { tasksList ->
                calculateStatistics(tasksList)
            }
        }
    }

    // --- Task Management ---
    fun addTask(title: String, timingMode: TimingMode, targetMinutes: Int, timeInterval: TimeInterval) {
        val newTask = StudyTask(
            id = UUID.randomUUID().toString(), title = title,
            type = if (timingMode == TimingMode.NONE) TaskType.HABIT else TaskType.TIMED_GOAL,
            timingMode = timingMode, targetMinutes = if (timingMode != TimingMode.NONE) targetMinutes else 0,
            timeInterval = timeInterval,
            targetCount = if (timingMode == TimingMode.NONE) 1 else 0,
            backgroundColor = Color( (0..255).random(), (0..255).random(), (0..255).random() ).copy(alpha = 1f),
            isCompleted = false, creationTimestamp = System.currentTimeMillis()
        )
        _tasks.update { listOf(newTask) + it }
    }

    fun addHabitTask(habitName: String) {
        val newTask = StudyTask(
            id = UUID.randomUUID().toString(),
            title = habitName,
            type = TaskType.HABIT,
            timingMode = TimingMode.NONE,
            timeInterval = TimeInterval.MORNING,
            targetCount = 1,
            backgroundColor = Color( (0..255).random(), (0..255).random(), (0..255).random()).copy(alpha = 1f),
            isCompleted = false,
            creationTimestamp = System.currentTimeMillis()
        )
        _tasks.update { listOf(newTask) + it }
    }


    // --- Timer Control ---
    fun startTimerForTask(taskId: String) {
        stopTimer()
        val task = _tasks.value.find { it.id == taskId } ?: return
        if (task.timingMode == TimingMode.NONE) {
            completeTask(taskId, markAsCompleted = true)
            return
        }
        val targetMillis = TimeUnit.MINUTES.toMillis(task.targetMinutes.toLong())
        _timerState.update {
            TimerState( activeTaskId = task.id, taskTitle = task.title, timingMode = task.timingMode,
                targetMillis = targetMillis, elapsedTimeMillis = 0,
                remainingTimeMillis = if (task.timingMode == TimingMode.COUNTDOWN) targetMillis else 0,
                isRunning = true, isPaused = false
            )
        }
        when (task.timingMode) {
            TimingMode.COUNTDOWN -> startCountdown(targetMillis)
            TimingMode.FORWARD -> startStopwatch()
            TimingMode.NONE -> {}
        }
    }

    private fun startCountdown(targetMillis: Long) {
        countdownTimer?.cancel()
        countdownTimer = object : CountDownTimer(targetMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) { if (!_timerState.value.isPaused) { _timerState.update { it.copy( remainingTimeMillis = millisUntilFinished, elapsedTimeMillis = targetMillis - millisUntilFinished ) } } }
            override fun onFinish() {
                val finishedTaskId = _timerState.value.activeTaskId
                if (finishedTaskId != null) {
                    val task = _tasks.value.find { it.id == finishedTaskId }
                    if (task != null) { completeTask(finishedTaskId, minutesCompleted = task.targetMinutes, markAsCompleted = true) }
                }
            }
        }.start()
    }

    private fun startStopwatch() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var startTime = System.currentTimeMillis() - _timerState.value.elapsedTimeMillis
            while (_timerState.value.isRunning && !_timerState.value.isPaused) {
                val currentElapsedTime = System.currentTimeMillis() - startTime
                _timerState.update { it.copy(elapsedTimeMillis = currentElapsedTime) }
                val targetMillis = _timerState.value.targetMillis
                if (targetMillis > 0 && currentElapsedTime >= targetMillis) {
                    val taskId = _timerState.value.activeTaskId
                    if (taskId != null) { completeTask(taskId, minutesCompleted = TimeUnit.MILLISECONDS.toMinutes(targetMillis).toInt(), markAsCompleted = true) }
                    break
                }
                delay(1000)
            }
        }
    }

    fun pauseTimer() { if (!_timerState.value.isRunning || _timerState.value.isPaused) return; _timerState.update { it.copy(isPaused = true, pauseStartTimeMillis = System.currentTimeMillis()) } }
    fun resumeTimer() { if (!_timerState.value.isRunning || !_timerState.value.isPaused) return; _timerState.update { it.copy(isPaused = false, pauseStartTimeMillis = 0) } }
    fun stopTimer() {
        val currentState = _timerState.value; val taskId = currentState.activeTaskId
        if (taskId != null && currentState.isRunning) {
            val task = _tasks.value.find { it.id == taskId }
            if (task != null) {
                val minutesCompletedOnStop = when (task.timingMode) {
                    TimingMode.FORWARD -> TimeUnit.MILLISECONDS.toMinutes(currentState.elapsedTimeMillis).toInt()
                    TimingMode.COUNTDOWN -> TimeUnit.MILLISECONDS.toMinutes(task.targetMinutes.toLong() - currentState.remainingTimeMillis).toInt()
                    TimingMode.NONE -> 0
                }
                val shouldMarkCompleted = (task.timingMode != TimingMode.NONE && task.targetMinutes > 0 && (minutesCompletedOnStop >= task.targetMinutes || (task.completedMinutes + minutesCompletedOnStop) >= task.targetMinutes))
                completeTask(taskId, minutesCompleted = minutesCompletedOnStop, markAsCompleted = shouldMarkCompleted)
            }
        } else { _timerState.value = TimerState() }
        timerJob?.cancel(); countdownTimer?.cancel(); timerJob = null; countdownTimer = null
    }

    fun completeTask(taskId: String, minutesCompleted: Int = 0, markAsCompleted: Boolean) {
        _tasks.update { currentTasks ->
            currentTasks.map { task ->
                if (task.id == taskId) {
                    if (task.isCompleted && markAsCompleted) return@map task
                    var updatedTask = task
                    if (task.type == TaskType.TIMED_GOAL && minutesCompleted > 0) {
                        updatedTask = updatedTask.copy(focusCount = task.focusCount + 1)
                    }
                    if (task.timingMode == TimingMode.NONE) {
                        if (markAsCompleted) {
                            updatedTask = updatedTask.copy(completedCount = task.completedCount + 1)
                        }
                    } else {
                        val newCompletedMinutes = (task.completedMinutes + minutesCompleted).coerceAtMost(task.targetMinutes.takeIf { it > 0 } ?: Int.MAX_VALUE)
                        updatedTask = updatedTask.copy(completedMinutes = newCompletedMinutes)
                    }
                    val isNowCompleted = markAsCompleted ||
                            (task.timingMode == TimingMode.NONE && updatedTask.completedCount >= task.targetCount) ||
                            (task.timingMode != TimingMode.NONE && task.targetMinutes > 0 && updatedTask.completedMinutes >= task.targetMinutes)
                    if (isNowCompleted && !task.isCompleted) {
                        updatedTask = updatedTask.copy(isCompleted = true, completionTimestamp = System.currentTimeMillis())
                    } else if (isNowCompleted && task.isCompleted) {
                        updatedTask = updatedTask.copy(isCompleted = true)
                    } else {
                        updatedTask = updatedTask.copy(isCompleted = isNowCompleted)
                    }
                    updatedTask
                } else { task }
            }
        }
        if (_timerState.value.activeTaskId == taskId) {
            _timerState.value = TimerState()
            timerJob?.cancel()
            countdownTimer?.cancel()
            timerJob = null
            countdownTimer = null
        }
    }


    // --- Study Column & Q&A Functions ---
    fun getSubjectByName(name: String): Subject? { return _subjects.value.find { it.name == name } }
    fun getKnowledgePointById(kpId: String): KnowledgePoint? {
        return _subjects.value.asSequence()
            .flatMap { it.chapters }
            .flatMap { it.knowledgePoints }
            .find { it.id == kpId }
    }

    fun addKnowledgePoint(subjectName: String, chapterId: String, question: String, answer: String) {
        _subjects.update { currentSubjects ->
            currentSubjects.map { subject ->
                if (subject.name == subjectName) {
                    subject.copy(
                        chapters = subject.chapters.map { chapter ->
                            if (chapter.id == chapterId) {
                                val newKnowledgePoint = KnowledgePoint(
                                    id = UUID.randomUUID().toString(),
                                    text = question,
                                    details = answer
                                )
                                chapter.copy(knowledgePoints = chapter.knowledgePoints + newKnowledgePoint)
                            } else {
                                chapter
                            }
                        }
                    )
                } else {
                    subject
                }
            }
        }
    }

    // NEW: Function to add a new chapter
    fun addChapter(subjectName: String, chapterTitle: String) {
        _subjects.update { currentSubjects ->
            currentSubjects.map { subject ->
                if (subject.name == subjectName) {
                    val newChapter = Chapter(
                        id = UUID.randomUUID().toString(),
                        title = chapterTitle,
                        knowledgePoints = emptyList()
                    )
                    subject.copy(chapters = subject.chapters + newChapter)
                } else {
                    subject
                }
            }
        }
    }


    // --- Diary Functions ---
    fun getDiaryEntry(date: Calendar): String? {
        val dateString = diaryDateFormatter.format(date.time)
        return _diaryEntries.value[dateString]
    }

    fun saveDiaryEntry(date: Calendar, content: String) {
        val dateString = diaryDateFormatter.format(date.time)
        _diaryEntries.update { currentEntries ->
            currentEntries.toMutableMap().apply { this[dateString] = content }.toMap()
        }
    }

    fun addInsightToDiary(insightText: String) {
        if (insightText.isBlank()) return
        val todayCalendar = Calendar.getInstance()
        val todayString = diaryDateFormatter.format(todayCalendar.time)
        val currentTimeString = insightTimeFormatter.format(todayCalendar.time)
        val formattedInsight = "$currentTimeString - $insightText"
        val existingEntry = _diaryEntries.value[todayString] ?: ""
        val updatedEntry = if (existingEntry.isBlank()) formattedInsight else "$existingEntry\n$formattedInsight"
        saveDiaryEntry(todayCalendar, updatedEntry)
    }


    // --- Diary Calendar Helpers ---
    fun getCurrentMonthCalendar(): Calendar { return Calendar.getInstance() }
    fun formatMonthYear(calendar: Calendar): String { return monthYearFormatter.format(calendar.time) }
    fun getDaysInMonth(calendar: Calendar): Int { return calendar.getActualMaximum(Calendar.DAY_OF_MONTH) }
    fun getFirstOfMonthCalendar(calendar: Calendar): Calendar { val firstDay = calendar.clone() as Calendar; firstDay.set(Calendar.DAY_OF_MONTH, 1); return firstDay }
    fun getDayOfWeek(calendar: Calendar): Int { return calendar.get(Calendar.DAY_OF_WEEK) }
    fun getFirstDayOfWeekPreference(): Int { return Calendar.getInstance().firstDayOfWeek }
    fun getShortDayNames(): List<String> {
        val firstDayPref = getFirstDayOfWeekPreference()
        val calendar = Calendar.getInstance()
        return (0..6).map { calendar.set(Calendar.DAY_OF_WEEK, (firstDayPref + it - 1) % 7 + 1); dayOfWeekFormatter.format(calendar.time) }
    }
    fun getCalendarForDay(monthCalendar: Calendar, dayOfMonth: Int): Calendar { val dayCal = monthCalendar.clone() as Calendar; dayCal.set(Calendar.DAY_OF_MONTH, dayOfMonth); return dayCal }
    fun isToday(calendar: Calendar): Boolean { val today = Calendar.getInstance(); return calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) && calendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH) }
    fun formatCalendarToIsoString(calendar: Calendar): String { return diaryDateFormatter.format(calendar.time) }
    fun parseIsoStringToCalendar(dateString: String): Calendar? { try { val date = diaryDateFormatter.parse(dateString); if (date != null) { val cal = Calendar.getInstance(); cal.time = date; return cal } else { return null } } catch (e: Exception) { return null } }


    // --- Statistics Calculation ---
    private fun calculateStatistics(tasks: List<StudyTask>) {
        val now = Calendar.getInstance()
        var currentPeriodTodayTime = 0
        var currentPeriodWeekTime = 0
        var currentPeriodMonthTime = 0
        var currentPeriodYearTime = 0
        var grandTotalAccumulatedTime = 0
        var todayCompletedCount = 0
        var weekCompletedCount = 0
        var monthCompletedCount = 0
        var yearCompletedCount = 0
        var totalCompletedTaskCount = 0

        tasks.forEach { task ->
            val taskDuration = task.completedMinutes
            if (task.isCompleted && task.completionTimestamp != null) {
                totalCompletedTaskCount++
                grandTotalAccumulatedTime += taskDuration
                val completionCal = Calendar.getInstance().apply { timeInMillis = task.completionTimestamp!! }
                if (isSameDay(completionCal, now)) { currentPeriodTodayTime += taskDuration; todayCompletedCount++ }
                if (isSameWeek(completionCal, now)) { currentPeriodWeekTime += taskDuration; weekCompletedCount++ }
                if (isSameMonth(completionCal, now)) { currentPeriodMonthTime += taskDuration; monthCompletedCount++ }
                if (isSameYear(completionCal, now)) { currentPeriodYearTime += taskDuration; yearCompletedCount++ }
            } else if (!task.isCompleted && taskDuration > 0) {
                currentPeriodTodayTime += taskDuration
                currentPeriodWeekTime += taskDuration
                currentPeriodMonthTime += taskDuration
                currentPeriodYearTime += taskDuration
                grandTotalAccumulatedTime += taskDuration
            }
        }

        _taskStatistics.update {
            TaskStatistics(
                completedToday = todayCompletedCount, completedTodayTime = currentPeriodTodayTime,
                completedThisWeek = weekCompletedCount, completedThisWeekTime = currentPeriodWeekTime,
                completedThisMonth = monthCompletedCount, completedThisMonthTime = currentPeriodMonthTime,
                completedThisYear = yearCompletedCount, completedThisYearTime = currentPeriodYearTime,
                completedTotal = totalCompletedTaskCount, completedTotalTime = grandTotalAccumulatedTime
            )
        }
    }

    fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    fun isSameWeek(cal1: Calendar, cal2: Calendar): Boolean = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR)
    fun isSameMonth(cal1: Calendar, cal2: Calendar): Boolean = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
    fun isSameYear(cal1: Calendar, cal2: Calendar): Boolean = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)


    // --- Recitation Functions (from V2.1) ---
    fun getRecitationStateFor(knowledgePointId: String): RecitationState {
        return _recitationStates.value[knowledgePointId] ?: RecitationState(knowledgePointId = knowledgePointId)
    }

    fun isRecitationDue(knowledgePointId: String): Boolean {
        val state = getRecitationStateFor(knowledgePointId)
        return state.nextReviewTimestamp <= System.currentTimeMillis()
    }

    fun updateRecitationState(knowledgePointId: String, rating: FamiliarityLevel) {
        val currentState = getRecitationStateFor(knowledgePointId)
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance().apply { timeInMillis = now }
        when (rating) {
            FamiliarityLevel.FORGOTTEN -> calendar.add(Calendar.MINUTE, 10)
            FamiliarityLevel.DIFFICULT -> calendar.add(Calendar.DAY_OF_YEAR, 1)
            FamiliarityLevel.FAMILIAR -> calendar.add(Calendar.DAY_OF_YEAR, 3)
            FamiliarityLevel.MASTERED -> calendar.add(Calendar.DAY_OF_YEAR, 7)
        }
        val nextReviewTimestamp = calendar.timeInMillis
        val newState = currentState.copy(
            familiarity = rating,
            lastReviewedTimestamp = now,
            nextReviewTimestamp = nextReviewTimestamp,
            reviewCount = currentState.reviewCount + 1
        )
        _recitationStates.update { it + (knowledgePointId to newState) }
    }

    suspend fun getAiGeneratedExplanation(knowledgePointText: String): String {
        delay(800)
        return "【AI 生成的解释】\n\n关于“${knowledgePointText}”的详细说明：\n\n1.  **核心定义**: [此处为AI生成的关于该知识点的核心定义...]\n2.  **重要特征**: [此处为AI生成的关于该知识点的重要特征或构成要素...]\n3.  **案例应用**: [此处为AI生成的一个或多个实际案例，用以说明该知识点的应用...]\n\n (这是一个模拟的AI回复。在实际应用中，这里会由真实的大语言模型生成。)"
    }

    // --- MERGED: Q&A Feature Logic (from MainActivity.kt) ---
    fun getTeacherQuestionsFor(knowledgePointId: String): List<TeacherQuestion> {
        return _teacherQuestions.value
            .filter { it.knowledgePointId == knowledgePointId }
            .sortedByDescending { it.timestamp }
    }

    fun askQuestion(knowledgePointId: String, questionText: String, author: String) {
        val newQuestion = TeacherQuestion(
            id = UUID.randomUUID().toString(),
            knowledgePointId = knowledgePointId,
            studentQuestion = questionText,
            authorName = author,
            timestamp = System.currentTimeMillis(),
            answers = emptyList() // 新问题没有任何回答
        )
        _teacherQuestions.update { listOf(newQuestion) + it }
    }

    fun getTeacherQuestionById(questionId: String): TeacherQuestion? { // Renamed
        return _teacherQuestions.value.find { it.id == questionId }
    }

    fun addAnswer(questionId: String, answerContent: String, authorName: String) {
        _teacherQuestions.update { currentQuestions ->
            currentQuestions.map { question ->
                if (question.id == questionId) {
                    val newAnswer = Answer(
                        questionId = questionId,
                        content = answerContent,
                        authorName = authorName,
                        status = AnswerStatus.PENDING // 新回答的状态默认为“待审核”
                    )
                    question.copy(answers = question.answers + newAnswer)
                } else {
                    question
                }
            }
        }
    }

    fun approveAnswer(questionId: String, answerId: String) {
        _teacherQuestions.update { currentQuestions ->
            currentQuestions.map { question ->
                if (question.id == questionId) {
                    question.copy(
                        answers = question.answers.map { answer ->
                            if (answer.id == answerId) {
                                answer.copy(status = AnswerStatus.APPROVED) // 修改状态为“已批准”
                            } else {
                                answer
                            }
                        }
                    )
                } else {
                    question
                }
            }
        }
    }
    // --- NEW: Practice Question Logic ---
    fun getQuestionsForKnowledgePoint(kpId: String): List<PracticeQuestion> {
        return _practiceQuestions.value.filter { it.knowledgePointId == kpId }
    }

    fun getPracticeQuestionById(questionId: String): PracticeQuestion? { // Renamed
        return _practiceQuestions.value.find { it.id == questionId }
    }

    fun addPracticeQuestion(question: PracticeQuestion) {
        _practiceQuestions.update { it + question }
    }

// --- NEW: FSRS Logic for Practice Questions ---

    fun getPracticeQuestionStateFor(questionId: String): PracticeQuestionState {
        return _practiceQuestionStates.value[questionId] ?: PracticeQuestionState(questionId = questionId)
    }

    fun isPracticeQuestionDue(questionId: String): Boolean {
        val state = getPracticeQuestionStateFor(questionId)
        // A question is due if its review time is in the past and it has been reviewed at least once.
        // New questions (reviewCount == 0) are not considered "due" in review mode.
        return state.nextReviewTimestamp <= System.currentTimeMillis() && state.reviewCount > 0
    }

    fun updatePracticeQuestionState(questionId: String, isCorrect: Boolean) {
        val currentState = getPracticeQuestionStateFor(questionId)
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance().apply { timeInMillis = now }

        val newFamiliarity = if (isCorrect) FamiliarityLevel.MASTERED else FamiliarityLevel.FORGOTTEN

        // FSRS-like interval calculation
        when (newFamiliarity) {
            FamiliarityLevel.FORGOTTEN -> calendar.add(Calendar.MINUTE, 10) // If wrong, review soon
            FamiliarityLevel.MASTERED -> {
                // Increase interval based on review count, e.g., 1, 3, 7, 14 days
                val daysToAdd = when (currentState.reviewCount) {
                    0 -> 1
                    1 -> 3
                    2 -> 7
                    3 -> 14
                    else -> 30
                }
                calendar.add(Calendar.DAY_OF_YEAR, daysToAdd)
            }
            else -> calendar.add(Calendar.DAY_OF_YEAR, 1) // Default case
        }

        val newState = currentState.copy(
            familiarity = newFamiliarity,
            lastReviewedTimestamp = now,
            nextReviewTimestamp = calendar.timeInMillis,
            reviewCount = currentState.reviewCount + 1
        )
        _practiceQuestionStates.update { it + (questionId to newState) }
    }

// --- NEW: AI Helper functions for Quizzes ---

    suspend fun getAiGeneratedHint(question: PracticeQuestion): String {
        delay(700) // Simulate network call
        return "【AI提示】\n这道题考察的是“${getKnowledgePointById(question.knowledgePointId)?.text}”这一知识点。请尝试回忆该知识点的核心定义，特别是与题目相关的部分。例如，如果是犯罪构成，思考是哪个要件出了问题？"
    }

    suspend fun getAiRephrasedAnalysis(originalAnalysis: String): String {
        delay(900) // Simulate network call
        return "【AI换一种方式解释】\n好的，我们换个角度来理解：\n\n$originalAnalysis\n\n**简而言之**，这意味着[此处为AI生成的对原解析的简化或比喻性解释]。想象一个场景：[此处为AI生成的一个更通俗的例子]。希望这样能帮助你更好地理解！\n(这是一个模拟的AI回复。)"
    }



    // --- Sample Data ---
    private fun getSampleTasks(): List<StudyTask> {
        val timeNow = System.currentTimeMillis()
        return listOf(
            StudyTask(id = "1", title = "英语", type = TaskType.TIMED_GOAL, timeInterval = TimeInterval.MORNING, timingMode = TimingMode.FORWARD, targetMinutes = 3000, completedMinutes = 1367, focusCount = 1, backgroundImageUrl = "https://placehold.co/600x100/003366/FFFFFF?text=Background+1", isCompleted = false, creationTimestamp = timeNow - 20000),
            StudyTask(id = "2", title = "民法", type = TaskType.TIMED_GOAL, timeInterval = TimeInterval.AFTERNOON, timingMode = TimingMode.COUNTDOWN, targetMinutes = 1, completedMinutes = 0, backgroundImageUrl = "https://placehold.co/600x100/006666/FFFFFF?text=Background+2", isCompleted = false, creationTimestamp = timeNow - 10000),
            StudyTask(id = "5", title = "法理", type = TaskType.TIMED_GOAL, timeInterval = TimeInterval.EVENING, timingMode = TimingMode.FORWARD, targetMinutes = 60, completedMinutes = 0, backgroundColor = Color(0xFFFFF0F5), isCompleted = false, creationTimestamp = timeNow - 5000),
            StudyTask(id = "3", title = "刑法", type = TaskType.TIMED_GOAL, timeInterval = TimeInterval.MORNING, timingMode = TimingMode.FORWARD, targetMinutes = 120, completedMinutes = 120, backgroundColor = Color(0xFFADD8E6), isCompleted = true, creationTimestamp = timeNow - 30000, completionTimestamp = timeNow - 5000), // Completed 5 secs ago
            StudyTask(id = "4", title = "政治", type = TaskType.HABIT, timeInterval = TimeInterval.MORNING, timingMode = TimingMode.NONE, targetCount = 1, completedMinutes = 0, backgroundColor = Color(0xFFFFFACD), isCompleted = false, creationTimestamp = timeNow), // Kept habit task
            StudyTask(id = "6", title = "昨日任务", type = TaskType.TIMED_GOAL, timeInterval = TimeInterval.MORNING, timingMode = TimingMode.FORWARD, targetMinutes = 60, completedMinutes = 60, backgroundColor = Color(0xFFE6E6FA), isCompleted = true, creationTimestamp = timeNow - (25 * 60 * 60 * 1000), completionTimestamp = timeNow - (24 * 60 * 60 * 1000 + 10000)), // Completed yesterday
            StudyTask(id = "7", title = "上周任务", type = TaskType.HABIT, timeInterval = TimeInterval.AFTERNOON, timingMode = TimingMode.NONE, targetCount = 1, completedCount = 1, completedMinutes = 0, backgroundColor = Color(0xFFFAFAD2), isCompleted = true, creationTimestamp = timeNow - (8 * 24 * 60 * 60 * 1000), completionTimestamp = timeNow - (7 * 24 * 60 * 60 * 1000 + 20000)), // Completed last week
            StudyTask(id = "8", title = "今日完成-短时", type = TaskType.TIMED_GOAL, timeInterval = TimeInterval.NOON, timingMode = TimingMode.COUNTDOWN, targetMinutes = 15, completedMinutes = 15, backgroundColor = Color(0xFF90EE90), isCompleted = true, creationTimestamp = timeNow - (2 * 60 * 60 * 1000), completionTimestamp = timeNow - (1 * 60 * 60 * 1000)), // Completed today, 15 mins
            StudyTask(id = "9", title = "今日完成-长时", type = TaskType.TIMED_GOAL, timeInterval = TimeInterval.MORNING, timingMode = TimingMode.FORWARD, targetMinutes = 75, completedMinutes = 75, backgroundColor = Color(0xFF98FB98), isCompleted = true, creationTimestamp = timeNow - (4 * 60 * 60 * 1000), completionTimestamp = timeNow - (30 * 60 * 1000)) // Completed today, 75 mins
        )
    }
    private fun getSampleSubjects(): List<Subject> {
        return listOf(
            Subject("subj1", "刑法", Icons.Filled.Gavel, listOf(
                Chapter("chap1_1", "第一章：刑法概述", listOf(
                    KnowledgePoint("kp1_1_1", "刑法的概念与特征", details = "刑法是规定犯罪、刑事责任和刑罚的法律规范的总称。其特征包括：调整范围的特定性、制裁方法的严厉性、渊源的单一性（罪刑法定）。"),
                    KnowledgePoint("kp1_1_2", "刑法的基本原则", details = "1. 罪刑法定原则\n2. 法律面前人人平等原则\n3. 罪责刑相适应原则")
                )),
                Chapter("chap1_2", "第二章：犯罪构成", listOf(
                    KnowledgePoint("kp1_2_1", "犯罪客体", details = "犯罪客体是刑法所保护的，而为犯罪行为所侵犯的社会主义社会关系。"),
                    KnowledgePoint("kp1_2_2", "犯罪客观方面", details = "指犯罪活动的客观外在表现，包括危害行为、危害结果以及它们之间的因果关系等。"),
                    KnowledgePoint("kp1_2_3", "犯罪主体", details = "指实施犯罪并依法承担刑事责任的自然人和单位。"),
                    KnowledgePoint("kp1_2_4", "犯罪主观方面", details = "指犯罪主体对自己实施的危害行为及其危害结果所持的心理态度，包括故意和过失。")
                ))
            )),
            Subject("subj2", "民法", Icons.Filled.AccountBalance, listOf(
                Chapter("chap2_1", "第一章：民法总则", listOf(KnowledgePoint("kp2_1_1", "民事主体"), KnowledgePoint("kp2_1_2", "民事权利"))),
                Chapter("chap2_2", "第二章：物权法", listOf(KnowledgePoint("kp2_2_1", "所有权"), KnowledgePoint("kp2_2_2", "用益物权")))
            )),
            Subject("subj3", "宪法", Icons.Filled.AssuredWorkload, listOf(
                Chapter("chap3_1", "第一章：国家的基本制度", listOf(KnowledgePoint("kp3_1_1", "国体与政体"))),
                Chapter("chap3_2", "第二章：公民的基本权利与义务", listOf(KnowledgePoint("kp3_2_1", "平等权"), KnowledgePoint("kp3_2_2", "自由权")))
            )),
            Subject("subj4", "法理学", Icons.Filled.Balance, listOf(
                Chapter("chap4_1", "第一章：法的本体", listOf(KnowledgePoint("kp4_1_1", "法的概念"), KnowledgePoint("kp4_1_2", "法的要素"))),
                Chapter("chap4_2", "第二章：法的运行", listOf(KnowledgePoint("kp4_2_1", "立法"), KnowledgePoint("kp4_2_2", "司法")))
            ))
        )
    }
    private fun getSampleExperienceImages(): List<String> {
        return listOf(
            "https://placehold.co/300x300/E8117F/FFFFFF?text=经验分享1",
            "https://placehold.co/300x300/7C3AED/FFFFFF?text=经验分享2",
            "https://placehold.co/300x300/F59E0B/FFFFFF?text=经验分享3",
            "https://placehold.co/300x300/10B981/FFFFFF?text=经验分享4"
        )
    }
    private fun getSampleHabits(): List<PredefinedHabit> {
        return listOf(
            PredefinedHabit("早起", "增强自律性, 提高一天的效率", Icons.Filled.WbSunny),
            PredefinedHabit("早睡", "保证充足睡眠, 有助于身体修复和记忆", Icons.Filled.Bedtime),
            PredefinedHabit("物品用完立刻归位", "保持环境整洁, 减少寻找物品的时间", Icons.Filled.Checkroom),
            PredefinedHabit("整理一个角落", "逐步改善生活环境, 减轻整理压力", Icons.Filled.CleaningServices),
            PredefinedHabit("定期更换床单枕套", "保持卫生, 预防皮肤问题和过敏", Icons.Filled.Hotel),
            PredefinedHabit("清理一次过期食品", "预防食品安全问题, 保持厨房整潔", Icons.Filled.Kitchen),
            PredefinedHabit("浴室用完擦干水渍", "防止细菌滋生, 延长浴室设施寿命", Icons.Filled.Bathtub)
        )
    }
    // MERGED: Sample data for Teacher Q&A (from MainActivity.kt)
    private fun getSampleTeacherQuestions(): List<TeacherQuestion> {
        val q1Id = "tq1"
        val q2Id = "tq2"
        return listOf(
            TeacherQuestion(
                id = q1Id, knowledgePointId = "kp1_1_1",
                studentQuestion = "老师您好，刑法渊源的单一性具体是指什么？是指只有一部《刑法典》吗？",
                authorName = "好学的张三",
                timestamp = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1),
                answers = listOf(
                    Answer(
                        questionId = q1Id,
                        content = "这位同学问得很好！渊源的单一性，更准确地说是“罪刑法定”原则在渊源上的体现。它意味着，能够规定犯罪与刑罚的法律文件，只能是由最高立法机关（全国人大及其常委会）制定的法律。所以，“单一”指的是渊源的层级单一，而非数量上只有一部法典。",
                        authorName = "法学高人",
                        status = AnswerStatus.APPROVED,
                        timestamp = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(12)
                    ),
                    Answer(
                        questionId = q1Id,
                        content = "我补充一下，还包括全国人大常委会后续出台的单行刑法和刑法修正案。",
                        authorName = "爱思考的李四",
                        status = AnswerStatus.PENDING, // 这条是待审核的
                        timestamp = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1)
                    )
                )
            ),
            TeacherQuestion(
                id = q2Id, knowledgePointId = "kp1_1_1",
                studentQuestion = "严厉性是不是刑法区别于其他法律最主要的特征？",
                authorName = "爱思考的李四",
                timestamp = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(5),
                answers = listOf(
                    Answer(
                        questionId = q2Id,
                        content = "是的，可以说严厉性是刑法最显著的特征。刑法所规定的刑罚，如徒刑、死刑等，是对公民最严厉的强制措施，直接限制甚至剥夺人身自由乃至生命。这是民法、行政法等其他部门法所不具备的。这种严厉性也要求我们在适用刑法时必须格外审慎。",
                        authorName = "老师",
                        status = AnswerStatus.APPROVED
                    )
                )
            ),
            TeacherQuestion(
                id = "tq3", knowledgePointId = "kp2_1_1",
                studentQuestion = "非法人组织和法人有什么核心区别？",
                authorName = "求知的王五",
                timestamp = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(2),
                answers = emptyList() // 这个问题还没有回答
            )
        )
    }
    // NEW: Sample data for Practice Questions
    private fun getSamplePracticeQuestions(): List<PracticeQuestion> {
        val options1 = listOf(
            MultipleChoiceOption(text = "调整范围的特定性"),
            MultipleChoiceOption(text = "制裁方法的严厉性"),
            MultipleChoiceOption(text = "制裁方法的补偿性"),
            MultipleChoiceOption(text = "渊源的单一性")
        )
        val options2 = listOf(
            MultipleChoiceOption(text = "罪刑法定原则"),
            MultipleChoiceOption(text = "疑罪从无原则"),
            MultipleChoiceOption(text = "法律面前人人平等原则"),
            MultipleChoiceOption(text = "罪责刑相适应原则")
        )
        // More sample questions for a better experience
        val options3 = listOf(
            MultipleChoiceOption(text = "判例援引原则"),
            MultipleChoiceOption(text = "罪刑法定原则"),
            MultipleChoiceOption(text = "罪责刑相适应原则"),
            MultipleChoiceOption(text = "法律面前人人平等原则")
        )
        return listOf(
            MultipleChoiceQuestion(
                knowledgePointId = "kp1_1_1",
                questionText = "下列哪一项不属于刑法的特征？",
                options = options1,
                correctOptionId = options1[2].id, // "制裁方法的补偿性"
                analysis = "刑法的特征包括调整范围的特定性、制裁方法的严厉性、以及渊源的单一性。补偿性是民事责任的特征，而非刑事责任。"
            ),
            MultipleChoiceQuestion(
                knowledgePointId = "kp1_1_2",
                questionText = "我国刑法明确规定的基本原则不包括以下哪一项？",
                options = options3,
                correctOptionId = options3[0].id, // "判例援引原则"
                analysis = "判例援引原则不属于我国刑法明文规定的基本原则。我国是成文法国家，司法活动主要依据法律条文，而非判例。刑法的三大基本原则是罪刑法定、法律面前人人平等、罪责刑相适应。"
            ),
            MultipleChoiceQuestion(
                knowledgePointId = "kp1_1_2",
                questionText = "刑法的基本原则不包括以下哪一项？",
                options = options2,
                correctOptionId = options2[1].id, // "疑罪从无原则"
                analysis = "刑法的三大基本原则是：罪刑法定原则、法律面前人人平等原则、罪责刑相适应原则。疑罪从无是刑事诉讼法的重要原则，而非刑法基本原则。"
            ),
            ShortAnswerQuestion(
                knowledgePointId = "kp1_2_1",
                questionText = "简述犯罪客体的概念。",
                referenceAnswer = "犯罪客体是刑法所保护的，而为犯罪行为所侵犯的社会主义社会关系。它决定了犯罪的性质，是区分此罪与彼罪的根本标准。",
                analysis = "注意区分犯罪客体与犯罪对象。犯罪对象是犯罪行为直接作用的具体人或物，而犯罪客体是其背后所侵犯的社会关系。"
            ),
            ShortAnswerQuestion(
                knowledgePointId = "kp2_1_1",
                questionText = "简述法人的概念和成立条件。",
                referenceAnswer = "法人是具有民事权利能力和民事行为能力，依法独立享有民事权利和承担民事义务的组织。\n成立条件：\n1. 依法成立；\n2. 有自己的名称、组织机构、住所、财产或者经费；\n3. 能够独立承担民事责任。",
                analysis = "法人的核心特征在于“独立承担民事责任”，这是其区别于非法人组织的关键。"
            )
        )
    }
}

// --- Navigation State (Merged & Modified) ---
sealed class Screen(val route: String) {
    object TaskList : Screen("taskList")
    object StudyColumn : Screen("studyColumn")
    object Statistics : Screen("statistics")
    object Profile : Screen("profile")
    data class Timer(val taskId: String) : Screen("timer/{taskId}") {
        fun createRoute() = "timer/$taskId"
    }
    object DiaryCalendar : Screen("diaryCalendar")
    data class DiaryEntry(val dateString: String) : Screen("diaryEntry/{dateString}") {
        fun createRoute() = "diaryEntry/$dateString"
        fun getCalendar(viewModel: TaskViewModel): Calendar? = viewModel.parseIsoStringToCalendar(dateString)
    }
    object Forum : Screen("forum")
    object Insights : Screen("insights")
    object AddHabit : Screen("addHabit")
    object StudyPlan : Screen("studyPlan")
    object CustomStudyPlan : Screen("customStudyPlan")
    data class SubjectDetail(val subjectName: String) : Screen("subjectDetail/{subjectName}") {
        fun createRoute() = "subjectDetail/$subjectName"
    }
    object TopStudentExperience : Screen("topStudentExperience")

    data class StatisticsDetail(val periodType: String, val periodLabel: String) : Screen("statisticsDetail/{periodType}/{periodLabel}") {
        fun createRoute(): String = "statisticsDetail/$periodType/$periodLabel"
    }

    object Settings : Screen("settings")
    object About : Screen("about")

    // Screens for Recitation (from V2.1 - Your new desired flow)
    object RecitationSubjects : Screen("recitationSubjects")
    data class RecitationModeSelection(val subjectName: String) : Screen("recitationModeSelection/{subjectName}") {
        fun createRoute() = "recitationModeSelection/$subjectName"
    }
    data class RecitationChapterList(val subjectName: String, val mode: String) : Screen("recitationChapterList/{subjectName}/{mode}") {
        fun createRoute() = "recitationChapterList/$subjectName/$mode"
    }
    data class RecitationCard(val kpId: String) : Screen("recitationCard/{kpId}") {
        fun createRoute() = "recitationCard/$kpId"
    }

    // Screen for adding a knowledge point
    data class AddKnowledgePoint(val subjectName: String, val chapterId: String? = null) : Screen("addKnowledgePoint/{subjectName}?chapterId={chapterId}") {
        fun createRoute(preselectedChapterId: String? = null): String {
            val baseRoute = "addKnowledgePoint/$subjectName"
            val finalChapterId = preselectedChapterId ?: chapterId
            return if (finalChapterId != null) {
                "$baseRoute?chapterId=$finalChapterId"
            } else {
                baseRoute
            }
        }
    }


    // MERGED: Screens for Q&A Feature (from MainActivity.kt)
    object QaSubjectList : Screen("qaSubjectList")
    data class QaSubjectDetail(val subjectName: String) : Screen("qaSubjectDetail/{subjectName}") {
        fun createRoute() = "qaSubjectDetail/$subjectName"
    }
    data class QaOptions(val subjectName: String, val knowledgePointId: String) : Screen("qaOptions/{subjectName}/{knowledgePointId}") {
        fun createRoute() = "qaOptions/$subjectName/$knowledgePointId"
    }
    data class QaSession(val subjectName: String, val knowledgePointId: String) : Screen("qaSession/{subjectName}/{knowledgePointId}") {
        fun createRoute() = "qaSession/$subjectName/$knowledgePointId"
    }
    data class TeacherQa(val subjectName: String, val knowledgePointId: String) : Screen("teacherQa/{subjectName}/{knowledgePointId}") {
        fun createRoute() = "teacherQa/$subjectName/$knowledgePointId"
    }
    data class QuestionDetail(val questionId: String) : Screen("questionDetail/{questionId}") {
        fun createRoute() = "questionDetail/$questionId"
    }

    // --- MODIFIED: Screens for Practice Questions (FSRS Flow) ---
    object PracticeSubjects : Screen("practiceSubjects") // Stays the same

    // NEW: Screen for selecting practice mode
    data class PracticeModeSelection(val subjectName: String) : Screen("practiceModeSelection/{subjectName}") {
        fun createRoute() = "practiceModeSelection/$subjectName"
    }

    // NEW: Screen for listing chapters to practice
    data class PracticeChapterList(val subjectName: String, val mode: String) : Screen("practiceChapterList/{subjectName}/{mode}") {
        fun createRoute() = "practiceChapterList/$subjectName/$mode"
    }

    // NEW: Screen for selecting a knowledge point before creating a question
    data class AddQuestionSubjectList(val subjectName: String) : Screen("addQuestionSubjectList/{subjectName}") {
        fun createRoute() = "addQuestionSubjectList/$subjectName"
    }


    // MODIFIED: Quiz session now takes subject and mode, not KP
    data class QuizSession(
        val subjectName: String,
        val mode: String,
        val chapterId: String? = null // NEW: Optional chapter ID
    ) : Screen("quizSession/{subjectName}/{mode}?chapterId={chapterId}") {
        fun createRoute(): String {
            val baseRoute = "quizSession/$subjectName/$mode"
            // If chapterId is provided, append it as a query parameter
            return if (chapterId != null) "$baseRoute?chapterId=$chapterId" else baseRoute
        }
    }
    data class CreateQuestion(val knowledgePointId: String) : Screen("createQuestion/{knowledgePointId}") {
        fun createRoute() = "createQuestion/$knowledgePointId"
    }
}

// --- MainActivity ---
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudyTimerAppTheme {
                AppScaffold()
            }
        }
    }
}

// --- UI Components ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(taskViewModel: TaskViewModel = viewModel()) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.TaskList) }
    var selectedBottomNavItem by remember { mutableStateOf(0) }
    var showAddMenu by remember { mutableStateOf(false) }
    var showAddToDoDialog by remember { mutableStateOf(false) }
    var showAddInsightDialog by remember { mutableStateOf(false) }

    var showAddTaskFromKPDialog by remember { mutableStateOf(false) }
    var initialKPTaskName by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val timerState by taskViewModel.timerState.collectAsState()
    val tasks by taskViewModel.sortedTasks.collectAsState()

    val isEditProfileOpen by taskViewModel.isEditProfileDialogOpen.collectAsState()
    val isLogoutConfirmOpen by taskViewModel.isLogoutConfirmOpen.collectAsState()
    val userProfile by taskViewModel.userProfile.collectAsState()

    if (isEditProfileOpen) {
        EditProfileDialog(
            userProfile = userProfile,
            onDismiss = { taskViewModel.closeEditProfileDialog() },
            onSave = { newNickname, newSignature ->
                taskViewModel.updateUserNickname(newNickname)
                taskViewModel.updateUserSignature(newSignature)
                taskViewModel.closeEditProfileDialog()
            }
        )
    }

    if (isLogoutConfirmOpen) {
        LogoutConfirmationDialog(
            onDismiss = { taskViewModel.closeLogoutConfirmDialog() },
            onConfirm = {
                taskViewModel.logout()
                Toast.makeText(context, "已退出登录", Toast.LENGTH_SHORT).show()
            }
        )
    }


    LaunchedEffect(timerState.activeTaskId) {
        if (currentScreen is Screen.Timer && timerState.activeTaskId == null) {
            delay(300)
            currentScreen = Screen.TaskList
            selectedBottomNavItem = 0
        }
    }

    val navItems = listOf("待办", "学习专栏", "数据统计", "我的")
    val navIcons = listOf(Icons.Filled.ListAlt, Icons.Filled.MenuBook, Icons.Filled.DonutSmall, Icons.Filled.Person)

    val openAddTaskDialogFromKP: (String) -> Unit = { prefilledName ->
        initialKPTaskName = prefilledName
        showAddTaskFromKPDialog = true
    }

    val navigationHistory = remember { mutableStateListOf<Screen>(Screen.TaskList) }

    val navigateTo: (Screen) -> Unit = { screen ->
        // This is a simplified resolver. A real app might use a more robust nav library.
        val resolvedScreen = when (screen) {
            is Screen.AddKnowledgePoint -> {
                val route = screen.createRoute()
                val subjectName = route.substringAfter("addKnowledgePoint/").substringBefore("?")
                val chapterId = if (route.contains("?chapterId=")) route.substringAfter("?chapterId=") else null
                Screen.AddKnowledgePoint(subjectName, chapterId)
            }
            else -> screen
        }


        if (navigationHistory.lastOrNull() != resolvedScreen) {
            if (currentScreen is Screen.Timer && resolvedScreen !is Screen.Timer && timerState.isRunning) {
                taskViewModel.stopTimer()
            }
            if (resolvedScreen is Screen.Timer) {
                val task = taskViewModel.sortedTasks.value.find { it.id == resolvedScreen.taskId }
                if (task != null && task.timingMode != TimingMode.NONE && !task.isCompleted) {
                    taskViewModel.startTimerForTask(resolvedScreen.taskId)
                    currentScreen = resolvedScreen
                    navigationHistory.add(resolvedScreen)
                } else if (task?.timingMode == TimingMode.NONE && !task.isCompleted) {
                    taskViewModel.completeTask(resolvedScreen.taskId, markAsCompleted = true)
                }
            } else {
                currentScreen = resolvedScreen
                navigationHistory.add(resolvedScreen)
            }
        }


        selectedBottomNavItem = when (currentScreen) {
            is Screen.TaskList, is Screen.Insights, is Screen.AddHabit -> 0
            is Screen.StudyColumn, is Screen.StudyPlan, is Screen.CustomStudyPlan,
            is Screen.SubjectDetail, is Screen.TopStudentExperience,
            is Screen.DiaryCalendar, is Screen.DiaryEntry,
            is Screen.Forum,
            is Screen.RecitationSubjects, is Screen.RecitationModeSelection,
            is Screen.RecitationChapterList, is Screen.RecitationCard, is Screen.AddKnowledgePoint,
            is Screen.QaSubjectList, is Screen.QaSubjectDetail, is Screen.QaOptions,
            is Screen.QaSession, is Screen.TeacherQa, is Screen.QuestionDetail, // Add Screen.QuestionDetail here
            is Screen.PracticeSubjects, is Screen.PracticeModeSelection, is Screen.PracticeChapterList,
            is Screen.AddQuestionSubjectList,
            is Screen.QuizSession, is Screen.CreateQuestion
                -> 1
            is Screen.Statistics, is Screen.StatisticsDetail -> 2
            is Screen.Profile, is Screen.Settings, is Screen.About -> 3
            is Screen.Timer -> selectedBottomNavItem
        }
    }

    val navigateBack: () -> Unit = {
        if (navigationHistory.size > 1) {
            navigationHistory.removeLastOrNull()

            currentScreen = navigationHistory.lastOrNull() ?: Screen.TaskList
        }

        selectedBottomNavItem = when (currentScreen) {
            is Screen.TaskList -> 0
            is Screen.StudyColumn -> 1
            is Screen.Statistics -> 2
            is Screen.Profile -> 3
            else -> selectedBottomNavItem
        }
    }


    if (showAddToDoDialog) { AddToDoDialog( onDismiss = { showAddToDoDialog = false }, onAddTask = { title, timingMode, duration, interval -> taskViewModel.addTask(title, timingMode, duration, interval); showAddToDoDialog = false } ) }
    if (timerState.isPaused && timerState.activeTaskId != null) { PauseDialog( pauseStartTimeMillis = timerState.pauseStartTimeMillis, onDismiss = { /* No action on dismiss */ }, onResume = { taskViewModel.resumeTimer() } ) }
    if (showAddInsightDialog) { AddInsightDialog( onDismiss = { showAddInsightDialog = false }, onSaveInsight = { insightText -> taskViewModel.addInsightToDiary(insightText); showAddInsightDialog = false; Toast.makeText(context, "感悟已保存到今日日记", Toast.LENGTH_SHORT).show() } ) }
    if (showAddTaskFromKPDialog) { AddToDoDialog( initialTaskName = initialKPTaskName, onDismiss = { showAddTaskFromKPDialog = false; initialKPTaskName = null }, onAddTask = { title, timingMode, duration, interval -> taskViewModel.addTask(title, timingMode, duration, interval); showAddTaskFromKPDialog = false; initialKPTaskName = null; Toast.makeText(context, "已添加待办: $title", Toast.LENGTH_SHORT).show() } ) }

    Scaffold(
        topBar = {
            if (currentScreen !is Screen.Timer) {
                CenterAlignedTopAppBar(
                    title = {
                        val topBarTitle = when (val screen = currentScreen) {
                            is Screen.TaskList -> "待办"
                            is Screen.StudyColumn -> "学习专栏"
                            is Screen.StudyPlan -> "学习规划"
                            is Screen.CustomStudyPlan -> "自定义学习规划"
                            is Screen.SubjectDetail -> screen.subjectName
                            is Screen.TopStudentExperience -> "学霸经验"
                            is Screen.DiaryCalendar -> "日记"
                            is Screen.DiaryEntry -> screen.dateString
                            is Screen.Statistics -> "数据统计"
                            is Screen.Profile -> "我的"
                            is Screen.Settings -> "设置"
                            is Screen.About -> "关于我们"
                            is Screen.Timer -> ""
                            is Screen.Forum -> "论坛"
                            is Screen.Insights -> "感悟"
                            is Screen.AddHabit -> "添加习惯打卡"
                            is Screen.StatisticsDetail -> screen.periodLabel
                            is Screen.RecitationSubjects -> "背书"
                            is Screen.RecitationModeSelection -> "选择模式 - ${screen.subjectName}"
                            is Screen.RecitationChapterList -> screen.subjectName
                            is Screen.RecitationCard -> "回忆中..."
                            is Screen.AddKnowledgePoint -> "添加知识点"
                            is Screen.QaSubjectList -> "选择答疑科目"
                            is Screen.QaSubjectDetail -> screen.subjectName
                            is Screen.QaOptions -> "选择答疑方式"
                            is Screen.QaSession -> "AI 问答"
                            is Screen.TeacherQa -> "老师答疑"
                            is Screen.QuestionDetail -> "问题详情" // Add title for the detail screen
                            // MODIFIED: Titles for Practice Question Screens
                            is Screen.PracticeSubjects -> "刷题"
                            is Screen.PracticeModeSelection -> "选择刷题模式"
                            is Screen.PracticeChapterList -> "选择章节"
                            is Screen.AddQuestionSubjectList -> "选择知识点"
                            is Screen.QuizSession -> "练习中..."
                            is Screen.CreateQuestion -> "创建新题目"
                        }
                        Text(topBarTitle, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    },
                    navigationIcon = {
                        val showBackButton = navigationHistory.size > 1 &&
                                currentScreen !is Screen.TaskList &&
                                currentScreen !is Screen.StudyColumn &&
                                currentScreen !is Screen.Statistics &&
                                currentScreen !is Screen.Profile
                        if (showBackButton) {
                            IconButton(onClick = navigateBack) {
                                Icon(Icons.Filled.ArrowBack, contentDescription = "返回")
                            }
                        }
                    },
                    actions = {
                        if (currentScreen == Screen.TaskList) {
                            Box {
                                IconButton(onClick = { showAddMenu = true }) { Icon(Icons.Filled.Add, contentDescription = "添加") }
                                DropdownMenu(expanded = showAddMenu, onDismissRequest = { showAddMenu = false }) {
                                    DropdownMenuItem( text = { Text("待办") }, leadingIcon = { Icon(Icons.Filled.Checklist, contentDescription = "待办") }, onClick = { showAddToDoDialog = true; showAddMenu = false } )
                                    DropdownMenuItem( text = { Text("感悟") }, leadingIcon = { Icon(Icons.Filled.Lightbulb, contentDescription = "感悟") }, onClick = { showAddInsightDialog = true; showAddMenu = false } )
                                    DropdownMenuItem( text = { Text("习惯") }, leadingIcon = { Icon(Icons.Filled.Repeat, contentDescription = "习惯") }, onClick = { navigateTo(Screen.AddHabit); showAddMenu = false } )
                                }
                            }
                        }
                        if (currentScreen is Screen.QuizSession) {
                            IconButton(onClick = { /* TODO: Implement timer feature in quiz */ }) {
                                Icon(Icons.Filled.Timer, contentDescription = "计时器")
                            }
                        }
                        IconButton(onClick = { /* TODO */ }) { Icon(Icons.Filled.MoreVert, contentDescription = "更多") }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        },
        bottomBar = {
            val isTopLevelScreen = currentScreen is Screen.TaskList ||
                    currentScreen is Screen.StudyColumn ||
                    currentScreen is Screen.Statistics ||
                    currentScreen is Screen.Profile
            if (isTopLevelScreen) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    navItems.forEachIndexed { index, title ->
                        NavigationBarItem(
                            icon = { Icon(navIcons[index], contentDescription = title) },
                            label = { Text(title) },
                            selected = selectedBottomNavItem == index,
                            onClick = {
                                if (selectedBottomNavItem != index) {
                                    val targetScreen = when (index) {
                                        0 -> Screen.TaskList
                                        1 -> Screen.StudyColumn
                                        2 -> Screen.Statistics
                                        3 -> Screen.Profile
                                        else -> Screen.TaskList
                                    }
                                    navigationHistory.clear()
                                    navigationHistory.add(targetScreen)
                                    currentScreen = targetScreen
                                    selectedBottomNavItem = index
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (val screen = currentScreen) {
                is Screen.TaskList -> TaskListScreen(tasks = tasks, navigateTo = navigateTo)
                is Screen.StudyColumn -> StudyColumnScreen(navigateTo = navigateTo)
                is Screen.Statistics -> StatisticsScreen(taskViewModel = taskViewModel, navigateTo = navigateTo)
                is Screen.Profile -> ProfileScreen(taskViewModel = taskViewModel, navigateTo = navigateTo)
                is Screen.Settings -> SettingsScreen() // 新增
                is Screen.About -> AboutScreen()       // 新增
                is Screen.Timer -> TimerScreen( timerState = timerState, onPause = { taskViewModel.pauseTimer() }, onResume = { taskViewModel.resumeTimer() }, onStop = { taskViewModel.stopTimer() } )
                is Screen.DiaryCalendar -> DiaryCalendarScreen(taskViewModel = taskViewModel, navigateTo = navigateTo)
                is Screen.DiaryEntry -> {
                    val calendar = screen.getCalendar(taskViewModel)
                    if (calendar != null) { DiaryEntryScreen( date = calendar, taskViewModel = taskViewModel, onSave = { _, content -> taskViewModel.saveDiaryEntry(calendar, content); navigateBack() } ) }
                    else { PlaceholderScreen("无效日期") }
                }
                is Screen.Forum -> PlaceholderScreen("论坛 (待实现)")
                is Screen.Insights -> PlaceholderScreen("感悟 (待实现)")
                is Screen.AddHabit -> AddHabitScreen(taskViewModel = taskViewModel, navigateTo = navigateTo)
                is Screen.StudyPlan -> StudyPlanScreen(navigateTo = navigateTo)
                is Screen.CustomStudyPlan -> CustomStudyPlanScreen(taskViewModel = taskViewModel, navigateTo = navigateTo)
                is Screen.SubjectDetail -> SubjectDetailScreen( subjectName = screen.subjectName, taskViewModel = taskViewModel, onKnowledgePointActionClick = openAddTaskDialogFromKP )
                is Screen.TopStudentExperience -> TopStudentExperienceScreen(taskViewModel = taskViewModel)
                is Screen.StatisticsDetail -> StatisticsDetailScreen( periodType = screen.periodType, periodLabel = screen.periodLabel, taskViewModel = taskViewModel )

                // Handle Recitation Screens
                is Screen.RecitationSubjects -> RecitationSubjectsScreen(taskViewModel = taskViewModel, navigateTo = navigateTo)
                is Screen.RecitationModeSelection -> RecitationModeSelectionScreen(subjectName = screen.subjectName, taskViewModel = taskViewModel, navigateTo = navigateTo)
                is Screen.RecitationChapterList -> RecitationChapterListScreen(subjectName = screen.subjectName, mode = screen.mode, taskViewModel = taskViewModel, navigateTo = navigateTo)
                is Screen.RecitationCard -> RecitationCardScreen(kpId = screen.kpId, taskViewModel = taskViewModel, onComplete = navigateBack)
                is Screen.AddKnowledgePoint -> AddKnowledgePointScreen(subjectName = screen.subjectName, preselectedChapterId = screen.chapterId, taskViewModel = taskViewModel, navigateBack = navigateBack)

                // Handle Q&A Screens
                is Screen.QaSubjectList -> QaSubjectListScreen(taskViewModel = taskViewModel, navigateTo = navigateTo)
                is Screen.QaSubjectDetail -> QaSubjectDetailScreen(subjectName = screen.subjectName, taskViewModel = taskViewModel, navigateTo = navigateTo)
                is Screen.QaOptions -> QaOptionsScreen(subjectName = screen.subjectName, knowledgePointId = screen.knowledgePointId, taskViewModel = taskViewModel, navigateTo = navigateTo)
                is Screen.QaSession -> QaSessionScreen(subjectName = screen.subjectName, knowledgePointId = screen.knowledgePointId, taskViewModel = taskViewModel)
                is Screen.TeacherQa -> TeacherQaScreen(subjectName = screen.subjectName, knowledgePointId = screen.knowledgePointId, taskViewModel = taskViewModel, navigateTo = navigateTo)
                is Screen.QuestionDetail -> QuestionDetailScreen(questionId = screen.questionId, taskViewModel = taskViewModel, navigateTo = navigateTo)

                // --- MODIFIED: Handle Practice Question Screens (NEW FSRS FLOW) ---
                is Screen.PracticeSubjects -> PracticeSubjectsScreen(
                    taskViewModel = taskViewModel,
                    navigateTo = navigateTo
                )
                is Screen.PracticeModeSelection -> PracticeModeSelectionScreen(
                    subjectName = screen.subjectName,
                    navigateTo = navigateTo
                )
                is Screen.PracticeChapterList -> PracticeChapterListScreen(
                    subjectName = screen.subjectName,
                    mode = screen.mode,
                    taskViewModel = taskViewModel,
                    navigateTo = navigateTo
                )
                // ADD THIS BLOCK
                is Screen.AddQuestionSubjectList -> AddQuestionSubjectListScreen(
                    subjectName = screen.subjectName,
                    taskViewModel = taskViewModel,
                    navigateTo = navigateTo
                )
                is Screen.QuizSession -> QuizSessionScreen(
                    subjectName = screen.subjectName,
                    mode = screen.mode,
                    chapterId = screen.chapterId, // ADDED
                    taskViewModel = taskViewModel,
                    onFinish = navigateBack
                )
                is Screen.CreateQuestion -> CreateQuestionScreen(
                    knowledgePointId = screen.knowledgePointId,
                    taskViewModel = taskViewModel,
                    onQuestionCreated = navigateBack
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToDoDialog(
    initialTaskName: String? = null,
    onDismiss: () -> Unit,
    onAddTask: (title: String, timingMode: TimingMode, durationMinutes: Int, timeInterval: TimeInterval) -> Unit
) {
    var taskName by remember { mutableStateOf(initialTaskName ?: "") }
    var selectedTimingMode by remember { mutableStateOf(TimingMode.COUNTDOWN) }
    var durationMinutesString by remember { mutableStateOf("25") }
    var selectedTimeInterval by remember { mutableStateOf(TimeInterval.MORNING) }
    val context = LocalContext.current
    val timeIntervalOptions = remember { TimeInterval.values() }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) { Icon(Icons.Filled.Close, contentDescription = "取消") }
                    Text("添加待办", style = MaterialTheme.typography.titleLarge)
                    IconButton(onClick = {
                        val duration = durationMinutesString.toIntOrNull() ?: 0
                        if (taskName.isNotBlank() && (selectedTimingMode == TimingMode.NONE || duration > 0)) {
                            onAddTask(taskName, selectedTimingMode, duration, selectedTimeInterval)
                        } else if (taskName.isBlank()) {
                            Toast.makeText(context, "请输入事项名称", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "请输入有效的时长（大于0）", Toast.LENGTH_SHORT).show()
                        }
                    }) { Icon(Icons.Filled.Check, contentDescription = "确认") }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = taskName, onValueChange = { taskName = it },
                    label = { Text("请输入事项名称") }, modifier = Modifier.fillMaxWidth(), singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    TimingModeButton(text = "倒计时", isSelected = selectedTimingMode == TimingMode.COUNTDOWN, onClick = { selectedTimingMode = TimingMode.COUNTDOWN })
                    TimingModeButton(text = "正向计时", isSelected = selectedTimingMode == TimingMode.FORWARD, onClick = { selectedTimingMode = TimingMode.FORWARD })
                    TimingModeButton(text = "不计时", isSelected = selectedTimingMode == TimingMode.NONE, onClick = { selectedTimingMode = TimingMode.NONE })
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (selectedTimingMode != TimingMode.NONE) {
                    OutlinedTextField(
                        value = durationMinutesString,
                        onValueChange = { if (it.all { c -> c.isDigit() }) durationMinutesString = it },
                        label = { Text("时长 (分钟)") }, modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                Text("选择区间", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    timeIntervalOptions.forEach { interval ->
                        Row(
                            Modifier
                                .weight(1f)
                                .selectable(
                                    selected = (interval == selectedTimeInterval),
                                    onClick = { selectedTimeInterval = interval },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 4.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton( selected = (interval == selectedTimeInterval), onClick = null )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text( text = interval.displayName, style = MaterialTheme.typography.bodyMedium )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddInsightDialog( onDismiss: () -> Unit, onSaveInsight: (insightText: String) -> Unit ) {
    var insightText by remember { mutableStateOf("") }
    val context = LocalContext.current
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("添加感悟", style = MaterialTheme.typography.titleLarge)
                    IconButton(onClick = onDismiss) { Icon(Icons.Filled.Close, contentDescription = "关闭") }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text( text = "感悟会保存在今日日记中", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField( value = insightText, onValueChange = { insightText = it }, modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp), label = { Text("记录你的想法...") }, placeholder = { Text("今天学到了什么？有什么新的理解？") }, shape = RoundedCornerShape(8.dp) )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (insightText.isNotBlank()) { onSaveInsight(insightText) }
                        else { Toast.makeText(context, "请输入感悟内容", Toast.LENGTH_SHORT).show() }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) { Text("保存") }
            }
        }
    }
}

@Composable
fun TimingModeButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors( containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent, contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary ),
        border = ButtonDefaults.outlinedButtonBorder.takeIf { !isSelected }
    ) { Text(text) }
}

@Composable
fun TaskListScreen( tasks: List<StudyTask>, modifier: Modifier = Modifier, navigateTo: (Screen) -> Unit ) {
    val (incompleteTasks, completedTasks) = tasks.partition { !it.isCompleted }
    val groupedIncompleteTasks = incompleteTasks.groupBy { it.timeInterval }.toSortedMap(compareBy { it.ordinal })
    LazyColumn( modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(12.dp) ) {
        groupedIncompleteTasks.forEach { (interval, tasksInInterval) ->
            item(key = "header-${interval.name}") { Text( text = interval.displayName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background).padding(vertical = 8.dp) ) }
            items(tasksInInterval, key = { task -> "incomplete-${task.id}" }) { task -> TaskItem( task = task, onStartClick = { navigateTo(Screen.Timer(task.id)) } ) }
        }
        if (completedTasks.isNotEmpty()) {
            item("divider") { Divider( modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant ) }
            item("completed-header") { Text( text = "已完成", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background).padding(vertical = 8.dp) ) }
            items(completedTasks, key = { task -> "completed-${task.id}" }) { task -> TaskItem( task = task, onStartClick = { /* Do nothing */ } ) }
        }
        if (tasks.isEmpty()) {
            item("empty-state") { Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) { Text("还没有待办事项，点击右上角添加吧！") } }
        } else if (incompleteTasks.isEmpty() && completedTasks.isNotEmpty()) {
            item("all-complete-state") { Box(modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp), contentAlignment = Alignment.Center) { Text("所有任务已完成！🎉") } }
        }
    }
}

@Composable
fun StudyColumnScreen(modifier: Modifier = Modifier, navigateTo: (Screen) -> Unit) {
    val items = listOf("学习规划", "背书", "答疑", "日记", "刷题", "论坛")
    val icons = listOf(Icons.Filled.EditNote, Icons.Filled.Psychology, Icons.Filled.QuestionAnswer, Icons.Filled.Book, Icons.Filled.Quiz, Icons.Filled.Forum)
    LazyVerticalGrid( columns = GridCells.Fixed(2), modifier = modifier.fillMaxSize().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp) ) {
        items(items.size) { index ->
            GridBlock(
                text = items[index],
                icon = icons[index],
                onClick = {
                    when (items[index]) {
                        "学习规划" -> navigateTo(Screen.StudyPlan)
                        "背书" -> navigateTo(Screen.RecitationSubjects)
                        "答疑" -> navigateTo(Screen.QaSubjectList)
                        "日记" -> navigateTo(Screen.DiaryCalendar)
                        "刷题" -> navigateTo(Screen.PracticeSubjects)
                        "论坛" -> navigateTo(Screen.Forum)
                        else -> { println("Clicked on: ${items[index]} (Not Implemented Yet)") }
                    }
                }
            )
        }
    }
}

// --- Recitation Feature Screens ---

@Composable
fun RecitationSubjectsScreen( modifier: Modifier = Modifier, taskViewModel: TaskViewModel, navigateTo: (Screen) -> Unit ) {
    val subjects by taskViewModel.subjects.collectAsState()
    LazyVerticalGrid( columns = GridCells.Fixed(2), modifier = modifier.fillMaxSize().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp) ) {
        items(subjects) { subject ->
            GridBlock( text = subject.name, icon = subject.icon, onClick = { navigateTo(Screen.RecitationModeSelection(subject.name)) } )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecitationModeSelectionScreen(
    subjectName: String,
    taskViewModel: TaskViewModel,
    modifier: Modifier = Modifier,
    navigateTo: (Screen) -> Unit
) {
    var isFabMenuExpanded by remember { mutableStateOf(false) }
    var showAddChapterDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Show the dialog when needed
    if (showAddChapterDialog) {
        AddChapterDialog(
            onDismiss = { showAddChapterDialog = false },
            onConfirm = { chapterTitle ->
                taskViewModel.addChapter(subjectName, chapterTitle)
                showAddChapterDialog = false
                isFabMenuExpanded = false
                Toast.makeText(context, "章节 “$chapterTitle” 已添加", Toast.LENGTH_SHORT).show()
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Animated sub-buttons for the Speed Dial menu
                AnimatedVisibility(
                    visible = isFabMenuExpanded,
                    enter = fadeIn(animationSpec = tween(150)) + slideInVertically { it / 2 },
                    exit = fadeOut(animationSpec = tween(150)) + slideOutVertically { it / 2 }
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // "Add Chapter" sub-button
                        SpeedDialFabItem(
                            icon = Icons.Default.CreateNewFolder,
                            label = "添加新章节",
                            onClick = { showAddChapterDialog = true }
                        )
                        // "Add Knowledge Point" sub-button
                        SpeedDialFabItem(
                            icon = Icons.Default.NoteAdd,
                            label = "添加知识点",
                            onClick = {
                                navigateTo(Screen.AddKnowledgePoint(subjectName, null))
                                isFabMenuExpanded = false
                            }
                        )
                    }
                }

                // Main Floating Action Button
                FloatingActionButton(
                    onClick = { isFabMenuExpanded = !isFabMenuExpanded },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        imageVector = if (isFabMenuExpanded) Icons.Filled.Close else Icons.Filled.Add,
                        contentDescription = if (isFabMenuExpanded) "关闭菜单" else "添加"
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("请为“$subjectName”选择背书模式", style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
            Spacer(Modifier.height(16.dp))
            ModeSelectionCard( icon = Icons.Filled.Sync, title = "复习模式", description = "仅复习今天到期的知识点", onClick = { navigateTo(Screen.RecitationChapterList(subjectName, "review")) } )
            ModeSelectionCard( icon = Icons.Filled.School, title = "学习模式", description = "按顺序学习所有知识点", onClick = { navigateTo(Screen.RecitationChapterList(subjectName, "learn")) } )
            ModeSelectionCard( icon = Icons.Filled.MergeType, title = "混合模式", description = "优先复习到期知识点，然后学习新知识点", onClick = { navigateTo(Screen.RecitationChapterList(subjectName, "hybrid")) } )
        }
    }
}

@Composable
fun PracticeModeSelectionScreen(
    subjectName: String,
    modifier: Modifier = Modifier,
    navigateTo: (Screen) -> Unit
) {
    var isFabMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Animated sub-button for "Add Question"
                AnimatedVisibility(
                    visible = isFabMenuExpanded,
                    enter = fadeIn(animationSpec = tween(150)) + slideInVertically { it / 2 },
                    exit = fadeOut(animationSpec = tween(150)) + slideOutVertically { it / 2 }
                ) {
                    SpeedDialFabItem(
                        icon = Icons.Default.NoteAdd,
                        label = "添加新题目",
                        onClick = {
                            navigateTo(Screen.AddQuestionSubjectList(subjectName))
                            isFabMenuExpanded = false
                        }
                    )
                }

                // Main Floating Action Button
                FloatingActionButton(
                    onClick = { isFabMenuExpanded = !isFabMenuExpanded },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        imageVector = if (isFabMenuExpanded) Icons.Filled.Close else Icons.Filled.Add,
                        contentDescription = if (isFabMenuExpanded) "关闭菜单" else "添加"
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("请为“$subjectName”选择刷题模式", style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
            Spacer(Modifier.height(16.dp))
            ModeSelectionCard(
                icon = Icons.Filled.Sync,
                title = "复习模式",
                description = "仅练习今天到期的题目",
                onClick = { navigateTo(Screen.QuizSession(subjectName, "review")) }
            )
            ModeSelectionCard(
                icon = Icons.Filled.School,
                title = "学习模式",
                description = "按顺序练习所有题目",
                onClick = { navigateTo(Screen.PracticeChapterList(subjectName, "learn")) }
            )
            ModeSelectionCard(
                icon = Icons.Filled.MergeType,
                title = "混合模式",
                description = "优先复习到期题目，再学习新题",
                onClick = { navigateTo(Screen.PracticeChapterList(subjectName, "hybrid")) }
            )
        }
    }
}
@Composable
fun PracticeChapterListScreen(
    subjectName: String,
    mode: String,
    taskViewModel: TaskViewModel,
    navigateTo: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val subject = remember(subjectName) { taskViewModel.getSubjectByName(subjectName) }
    val context = LocalContext.current // <-- 将 context 的获取移动到这里

    if (subject == null) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("错误：找不到科目 '$subjectName'") }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 提供一个选项来练习本科目的所有题目
        item {
            ElevatedCard(
                onClick = { navigateTo(Screen.QuizSession(subject.name, mode)) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Checklist,
                        contentDescription = "所有章节",
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(16.dp))
                    Text("练习所有章节", style = MaterialTheme.typography.titleLarge)
                }
            }
        }

        item {
            Divider(modifier = Modifier.padding(vertical = 16.dp))
        }

        // 列出每个章节供单独练习
        items(subject.chapters, key = { it.id }) { chapter ->
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = chapter.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                val questionsInChapter = taskViewModel.practiceQuestions.collectAsState().value
                    .filter { q -> chapter.knowledgePoints.any { kp -> kp.id == q.knowledgePointId } }

                if (questionsInChapter.isNotEmpty()) {
                    PracticeKnowledgePointItem(
                        knowledgePoint = KnowledgePoint(id = chapter.id, text = "练习本章 (${questionsInChapter.size}题)"),
                        onClick = {
                            // MODIFIED: Navigate with the specific chapterId
                            navigateTo(Screen.QuizSession(subject.name, mode, chapterId = chapter.id))
                        }
                    )
                } else {
                    Text("本章下暂无题目", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun AddQuestionSubjectListScreen(
    subjectName: String,
    taskViewModel: TaskViewModel,
    navigateTo: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val subject = remember(subjectName) { taskViewModel.getSubjectByName(subjectName) }
    if (subject == null) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("错误：找不到科目 '$subjectName'") }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "请选择一个知识点来添加题目",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        subject.chapters.forEach { chapter ->
            item(key = "header-${chapter.id}") {
                Text(
                    text = chapter.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )
            }
            items(chapter.knowledgePoints, key = { it.id }) { kp ->
                // We can reuse this existing composable for the item UI
                QaKnowledgePointItem(
                    knowledgePoint = kp,
                    onClick = { navigateTo(Screen.CreateQuestion(kp.id)) }
                )
            }
            item(key = "divider-${chapter.id}") {
                Divider(modifier = Modifier.padding(top = 16.dp))
            }
        }
    }
}

// Helper composable for the items in the speed dial menu
@Composable
private fun SpeedDialFabItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Text(label, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
        }
        FloatingActionButton(
            onClick = onClick,
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 2.dp)
        ) {
            Icon(imageVector = icon, contentDescription = label)
        }
    }
}


@Composable
fun ModeSelectionCard( icon: ImageVector, title: String, description: String, onClick: () -> Unit ) {
    ElevatedCard( onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp) ) {
        Row( modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically ) {
            Icon( imageVector = icon, contentDescription = title, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleLarge)
                Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun RecitationChapterListScreen( subjectName: String, mode: String, taskViewModel: TaskViewModel, navigateTo: (Screen) -> Unit, modifier: Modifier = Modifier ) {
    val subjectsState by taskViewModel.subjects.collectAsState()
    val currentSubject = remember(subjectsState, subjectName) {
        subjectsState.find { it.name == subjectName }
    }

    if (currentSubject == null) { Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("错误：找不到科目 '$subjectName'") }; return }

    val allKps = currentSubject.chapters.flatMap { it.knowledgePoints }
    val dueKps = allKps.filter { taskViewModel.isRecitationDue(it.id) }
    val (kpsToShow, title) = when (mode) {
        "review" -> dueKps to "今日需复习 (${dueKps.size}个)"
        "learn" -> allKps to "所有知识点 (${allKps.size}个)"
        "hybrid" -> (dueKps + allKps.filterNot { taskViewModel.isRecitationDue(it.id) }).distinctBy { it.id } to "混合模式 (${allKps.size}个)"
        else -> allKps to "所有知识点"
    }

    if (mode == "review" && kpsToShow.isEmpty()) { Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) { Text("太棒了！\n今天没有需要复习的项目。", textAlign = TextAlign.Center, style = MaterialTheme.typography.headlineSmall) }; return }

    LazyColumn( modifier = modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(16.dp) ) {
        item { Text( text = title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp) ) }
        if (mode != "review") {
            currentSubject.chapters.forEach { chapter ->
                item(key = "header-${chapter.id}") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = chapter.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        IconButton(onClick = {
                            // 直接创建带有确切 chapter.id 的 Screen 实例并导航
                            navigateTo(Screen.AddKnowledgePoint(subjectName, chapter.id))
                        }) {
                            Icon(Icons.Filled.AddCircleOutline, contentDescription = "为本章添加知识点", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
                items(chapter.knowledgePoints, key = { it.id }) { kp -> RecitationKnowledgePointItem( knowledgePoint = kp, isDue = taskViewModel.isRecitationDue(kp.id), onClick = { navigateTo(Screen.RecitationCard(kp.id)) } ) }
                item(key = "divider-${chapter.id}") { Divider(modifier = Modifier.padding(top = 16.dp)) }
            }
        } else {
            items(kpsToShow, key = { it.id }) { kp -> RecitationKnowledgePointItem( knowledgePoint = kp, isDue = true, onClick = { navigateTo(Screen.RecitationCard(kp.id)) } ) }
        }
    }
}


@Composable
fun RecitationKnowledgePointItem( knowledgePoint: KnowledgePoint, isDue: Boolean, onClick: () -> Unit ) {
    Card( modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) ) {
        Row( modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                if (isDue) { Box( modifier = Modifier.size(8.dp).background(MaterialTheme.colorScheme.primary, CircleShape) ); Spacer(modifier = Modifier.width(12.dp)) }
                Text( text = knowledgePoint.text, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(end = 8.dp) )
            }
            Icon( imageVector = Icons.Filled.ChevronRight, contentDescription = "开始背书", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) )
        }
    }
}

@Composable
fun RecitationCardScreen( kpId: String, taskViewModel: TaskViewModel, onComplete: () -> Unit ) {
    val knowledgePoint = remember(kpId) { taskViewModel.getKnowledgePointById(kpId) }
    var isAnswerVisible by remember { mutableStateOf(false) }
    var aiExplanation by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(knowledgePoint) {
        if (knowledgePoint != null) {
            isLoading = true
            aiExplanation = if (knowledgePoint.details.isNotBlank()) {
                knowledgePoint.details
            } else {
                taskViewModel.getAiGeneratedExplanation(knowledgePoint.text)
            }
            isLoading = false
        }
    }

    if (knowledgePoint == null) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("错误：找不到该知识点。") }; return }

    Column( modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("问题", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                Text(knowledgePoint.text, style = MaterialTheme.typography.headlineSmall)
            }
        }
        Spacer(Modifier.height(24.dp))
        if (isAnswerVisible) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(vertical = 32.dp))
                Text("AI 学习引擎正在生成解释...")
            } else {
                Card( modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant) ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(if (knowledgePoint.details.isNotBlank()) "答案" else "AI 解释", style = MaterialTheme.typography.labelMedium)
                        Spacer(Modifier.height(8.dp))
                        Text(aiExplanation ?: "加载中...", style = MaterialTheme.typography.bodyLarge, lineHeight = 24.sp)
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            Text("回忆的如何？请评估：", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))
            val familiarityLevels = FamiliarityLevel.values()
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                familiarityLevels.toList().chunked(2).forEach { rowLevels ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowLevels.forEach { level ->
                            Button(
                                onClick = { taskViewModel.updateRecitationState(kpId, level); onComplete() },
                                colors = ButtonDefaults.buttonColors(containerColor = level.color),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(60.dp)
                            ) {
                                Text(level.levelName, color = Color.White)
                            }
                        }
                        if (rowLevels.size < 2) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }

        } else {
            Button( onClick = { isAnswerVisible = true }, modifier = Modifier.fillMaxWidth().height(50.dp) ) { Text("显示答案") }
        }
    }
}


// --- Add Content Screens ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddKnowledgePointScreen(
    subjectName: String,
    preselectedChapterId: String?, // 传入的章节ID
    taskViewModel: TaskViewModel,
    navigateBack: () -> Unit
) {
    val subjectsState by taskViewModel.subjects.collectAsState()
    val subject = remember(subjectsState, subjectName) { subjectsState.find { it.name == subjectName } }
    val context = LocalContext.current

    if (subject == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("错误: 找不到科目 '$subjectName'") }
        return
    }

    // 直接通过传入的 ID 找到章节
    val chapter = remember(preselectedChapterId, subject.chapters) {
        subject.chapters.find { it.id == preselectedChapterId }
    }

    var questionText by remember { mutableStateOf("") }
    var answerText by remember { mutableStateOf("") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("为 “$subjectName” 添加新知识点", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 16.dp))

        OutlinedTextField(
            value = questionText,
            onValueChange = { questionText = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("问题或知识点名称") },
            placeholder = { Text("例如：什么是“罪刑法定原则”？") },
            shape = RoundedCornerShape(8.dp)
        )
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = answerText,
            onValueChange = { answerText = it },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp),
            label = { Text("答案或详细解释") },
            placeholder = { Text("在此输入详细的回答或解释内容...") },
            shape = RoundedCornerShape(8.dp)
        )
        Spacer(Modifier.height(24.dp))

        // 【修改】移除下拉菜单，替换为简单的文本显示
        Text("归属章节", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

// 显示当前章节名称，不可编辑
        OutlinedTextField(
            value = chapter?.title ?: "错误：未找到章节",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledContainerColor = Color.Transparent,
                disabledIndicatorColor = MaterialTheme.colorScheme.outline, // <-- Correct parameter name
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            enabled = false
        )

        Spacer(Modifier.height(32.dp))

        // 【修改】更新按钮的点击逻辑和启用条件
        Button(
            onClick = {
                when {
                    questionText.isBlank() -> { Toast.makeText(context, "请输入问题", Toast.LENGTH_SHORT).show() }
                    answerText.isBlank() -> { Toast.makeText(context, "请输入答案", Toast.LENGTH_SHORT).show() }
                    chapter == null -> { Toast.makeText(context, "错误：无法添加到指定章节", Toast.LENGTH_SHORT).show() }
                    else -> {
                        // 直接使用传入的 preselectedChapterId
                        taskViewModel.addKnowledgePoint( subjectName = subjectName, chapterId = chapter.id, question = questionText, answer = answerText )
                        Toast.makeText(context, "知识点已添加！", Toast.LENGTH_SHORT).show()
                        navigateBack()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            // 按钮仅在章节有效时才可点击
            enabled = chapter != null
        ) {
            Text("保存知识点")
        }
    }
}

// NEW: Dialog for adding a chapter
@Composable
fun AddChapterDialog(onDismiss: () -> Unit, onConfirm: (chapterTitle: String) -> Unit) {
    var chapterTitle by remember { mutableStateOf("") }
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp)) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("添加新章节", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = chapterTitle,
                    onValueChange = { chapterTitle = it },
                    label = { Text("章节名称") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("取消")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (chapterTitle.isNotBlank()) {
                                onConfirm(chapterTitle)
                            } else {
                                Toast.makeText(context, "章节名称不能为空", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("创建")
                    }
                }
            }
        }
    }
}


// --- MERGED: Q&A Feature Screens ---

@Composable
fun QaSubjectListScreen( modifier: Modifier = Modifier, taskViewModel: TaskViewModel, navigateTo: (Screen) -> Unit ) {
    val subjects by taskViewModel.subjects.collectAsState()
    LazyVerticalGrid( columns = GridCells.Fixed(2), modifier = modifier.fillMaxSize().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp) ) {
        items(subjects) { subject -> GridBlock( text = subject.name, icon = subject.icon, onClick = { navigateTo(Screen.QaSubjectDetail(subject.name)) } ) }
    }
}

@Composable
fun QaSubjectDetailScreen( subjectName: String, taskViewModel: TaskViewModel, navigateTo: (Screen) -> Unit, modifier: Modifier = Modifier ) {
    val subject = remember(subjectName) { taskViewModel.getSubjectByName(subjectName) }
    if (subject == null) { Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("错误：找不到科目 '$subjectName'") }; return }
    LazyColumn( modifier = modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(16.dp) ) {
        items(subject.chapters) { chapter -> QaChapterItem( chapter = chapter, onKnowledgePointClick = { kpId -> navigateTo(Screen.QaOptions(subjectName, kpId)) } ) }
    }
}

@Composable
fun QaChapterItem( chapter: Chapter, onKnowledgePointClick: (String) -> Unit, modifier: Modifier = Modifier ) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text( text = chapter.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp) )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            chapter.knowledgePoints.forEach { point -> QaKnowledgePointItem( knowledgePoint = point, onClick = { onKnowledgePointClick(point.id) } ) }
        }
        Divider(modifier = Modifier.padding(top = 16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QaKnowledgePointItem( knowledgePoint: KnowledgePoint, onClick: () -> Unit, modifier: Modifier = Modifier ) {
    Card( onClick = onClick, modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp) ) {
        Row( modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween ) {
            Text( text = "• ${knowledgePoint.text}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant )
            Icon( imageVector = Icons.Filled.ChevronRight, contentDescription = "选择", tint = MaterialTheme.colorScheme.primary )
        }
    }
}

@Composable
fun QaOptionsScreen( subjectName: String, knowledgePointId: String, taskViewModel: TaskViewModel, navigateTo: (Screen) -> Unit, modifier: Modifier = Modifier ) {
    val knowledgePoint = remember(knowledgePointId) { taskViewModel.getKnowledgePointById(knowledgePointId) }
    Column( modifier = modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center ) {
        knowledgePoint?.let { Text( text = "关于知识点", style = MaterialTheme.typography.titleMedium ); Text( text = "“${it.text}”", style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 32.dp, top = 8.dp) ) }
        QaOptionButton( text = "AI 答疑", description = "立即获得 AI 的解答", icon = Icons.Default.SmartToy, onClick = { navigateTo(Screen.QaSession(subjectName, knowledgePointId)) } )
        Spacer(modifier = Modifier.height(16.dp))
        QaOptionButton( text = "老师答疑", description = "查看历史问答或向老师提问", icon = Icons.Default.People, onClick = { navigateTo(Screen.TeacherQa(subjectName, knowledgePointId)) } )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QaOptionButton( text: String, description: String, icon: ImageVector, onClick: () -> Unit ) {
    Card( onClick = onClick, modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) ) {
        Row( modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically ) {
            Icon( imageVector = icon, contentDescription = text, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = text, style = MaterialTheme.typography.titleLarge)
                Text( text = description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant )
            }
            Icon( imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QaSessionScreen( subjectName: String, knowledgePointId: String, taskViewModel: TaskViewModel, modifier: Modifier = Modifier ) {
    val knowledgePoint = remember(knowledgePointId) { taskViewModel.getKnowledgePointById(knowledgePointId) }
    if (knowledgePoint == null) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("错误：找不到该知识点") }; return }

    var userQuestion by remember { mutableStateOf("") }
    val chatMessages = remember { mutableStateListOf<ChatMessage>() }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) { chatMessages.add( ChatMessage( text = "你好！我是您的 AI 答疑助手。请问关于 “${knowledgePoint.text}” 这个知识点，您有什么问题吗？", isFromUser = false ) ) }

    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Card( modifier = Modifier.fillMaxWidth().padding(8.dp), elevation = CardDefaults.cardElevation(2.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant) ) {
            Column(Modifier.padding(12.dp)) {
                Text(text = "当前知识点:", style = MaterialTheme.typography.labelMedium)
                Text( text = knowledgePoint.text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold )
                if (knowledgePoint.details.isNotBlank()) { Spacer(Modifier.height(4.dp)); Text( text = knowledgePoint.details, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis ) }
            }
        }
        LazyColumn( modifier = Modifier.weight(1f).padding(horizontal = 8.dp), reverseLayout = true, verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom) ) {
            items(chatMessages.reversed(), key = { it.id }) { message -> ChatMessageBubble(message = message) }
        }
        Row( modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(8.dp), verticalAlignment = Alignment.CenterVertically ) {
            OutlinedTextField( value = userQuestion, onValueChange = { userQuestion = it }, modifier = Modifier.weight(1f), placeholder = { Text("在此输入您的问题...") }, shape = RoundedCornerShape(24.dp) )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (userQuestion.isNotBlank()) {
                        val question = userQuestion
                        chatMessages.add(ChatMessage(text = question, isFromUser = true))
                        userQuestion = ""
                        coroutineScope.launch {
                            delay(1500)
                            val simulatedResponse = "关于您提出的“${question}”，我的理解是：[此处为AI生成内容]。根据知识点“${knowledgePoint.text}”的定义，我们可以从以下几个方面来分析...[更多AI生成内容]。希望这个解答对您有帮助！"
                            chatMessages.add(ChatMessage(text = simulatedResponse, isFromUser = false))
                        }
                    }
                },
                enabled = userQuestion.isNotBlank()
            ) { Icon( Icons.Filled.Send, contentDescription = "发送", tint = if (userQuestion.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) ) }
        }
    }
}

@Composable
fun ChatMessageBubble(message: ChatMessage) {
    Row( modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start ) {
        Card(
            shape = RoundedCornerShape( topStart = 16.dp, topEnd = 16.dp, bottomStart = if (message.isFromUser) 16.dp else 0.dp, bottomEnd = if (message.isFromUser) 0.dp else 16.dp ),
            colors = CardDefaults.cardColors( containerColor = if (message.isFromUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer ),
            modifier = Modifier.widthIn(max = 300.dp)
        ) { Text( text = message.text, modifier = Modifier.padding(12.dp), color = if (message.isFromUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer ) }
    }
}

@Composable
fun TeacherQaScreen(
    subjectName: String,
    knowledgePointId: String,
    taskViewModel: TaskViewModel,
    navigateTo: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val knowledgePoint = remember(knowledgePointId) { taskViewModel.getKnowledgePointById(knowledgePointId) }
    val questions by taskViewModel.teacherQuestions.collectAsState()
    val relevantQuestions = remember(questions) {
        taskViewModel.getTeacherQuestionsFor(knowledgePointId)
    }
    var newUserQuestion by remember { mutableStateOf("") }
    val context = LocalContext.current
    val userProfile by taskViewModel.userProfile.collectAsState()

    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // 问题列表
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                knowledgePoint?.let {
                    Text(
                        text = "关于 “${it.text}” 的全部问题",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
            if (relevantQuestions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "还没有人提问，你可以做第一个！",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(relevantQuestions, key = { it.id }) { question ->
                    QuestionSummaryItem(
                        question = question,
                        onClick = { navigateTo(Screen.QuestionDetail(question.id)) }
                    )
                }
            }
        }

        // 底部提问区域
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(8.dp),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "提出新问题",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = newUserQuestion,
                    onValueChange = { newUserQuestion = it },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp),
                    placeholder = { Text("在此输入你的问题...") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (newUserQuestion.isNotBlank()) {
                            taskViewModel.askQuestion(knowledgePointId, newUserQuestion, userProfile.nickname)
                            newUserQuestion = ""
                            Toast.makeText(context, "问题已提交！", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.align(Alignment.End),
                    enabled = newUserQuestion.isNotBlank()
                ) {
                    Text("提交问题")
                }
            }
        }
    }
}
@Composable
fun QuestionSummaryItem(question: TeacherQuestion, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = question.studentQuestion,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "提问者: ${question.authorName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${question.answers.count { it.status == AnswerStatus.APPROVED }}个回答",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionDetailScreen(
    questionId: String,
    taskViewModel: TaskViewModel,
    navigateTo: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val question by produceState<TeacherQuestion?>(initialValue = null, producer = {
        value = taskViewModel.getTeacherQuestionById(questionId) // Use the new function name
    })
    val userProfile by taskViewModel.userProfile.collectAsState()
    var showAddAnswerDialog by remember { mutableStateOf(false) }

    if (showAddAnswerDialog) {
        AddAnswerDialog(
            onDismiss = { showAddAnswerDialog = false },
            onSubmit = { answerContent ->
                taskViewModel.addAnswer(questionId, answerContent, userProfile.nickname)
                showAddAnswerDialog = false
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddAnswerDialog = true }) {
                Icon(Icons.Filled.Edit, contentDescription = "我来回答")
            }
        }
    ) { padding ->
        if (question == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 问题展示区
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("问题", style = MaterialTheme.typography.labelLarge)
                            Text(
                                question!!.studentQuestion,
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "提问者: ${question!!.authorName}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                // 已批准的回答
                val approvedAnswers = question!!.answers.filter { it.status == AnswerStatus.APPROVED }
                if (approvedAnswers.isNotEmpty()) {
                    item { Text("热门回答", style = MaterialTheme.typography.titleMedium) }
                    items(approvedAnswers, key = { "approved-${it.id}" }) { answer ->
                        AnswerItem(answer = answer, showApproveButton = false, onApprove = {})
                    }
                } else {
                    item {
                        Box(Modifier.fillMaxWidth().padding(vertical = 20.dp), Alignment.Center) {
                            Text("暂无回答，点击右下角按钮抢先回答！")
                        }
                    }
                }

                // 待审核的回答 (模拟管理员视角)
                val pendingAnswers = question!!.answers.filter { it.status == AnswerStatus.PENDING }
                if (pendingAnswers.isNotEmpty()) {
                    item {
                        Column(Modifier.padding(top = 16.dp)) {
                            Text("待审核回答 (管理员视角)", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "此处为模拟审核流程，点击“批准”可让回答公开显示。",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    items(pendingAnswers, key = { "pending-${it.id}" }) { answer ->
                        AnswerItem(
                            answer = answer,
                            showApproveButton = true,
                            onApprove = { taskViewModel.approveAnswer(questionId, answer.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnswerItem(
    answer: Answer,
    showApproveButton: Boolean,
    onApprove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (showApproveButton) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(answer.content, style = MaterialTheme.typography.bodyLarge, lineHeight = 24.sp)
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "回答者: ${answer.authorName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (showApproveButton) {
                    Button(onClick = onApprove, contentPadding = PaddingValues(horizontal = 12.dp)) {
                        Icon(Icons.Filled.Check, contentDescription = "批准", modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("批准")
                    }
                }
            }
        }
    }
}

@Composable
fun AddAnswerDialog(onDismiss: () -> Unit, onSubmit: (String) -> Unit) {
    var answerText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.padding(24.dp)) {
                Text("我来回答", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = answerText,
                    onValueChange = { answerText = it },
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    label = { Text("写下你的见解...") }
                )
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("取消") }
                    Button(
                        onClick = { onSubmit(answerText) },
                        enabled = answerText.isNotBlank()
                    ) {
                        Text("提交回答")
                    }
                }
            }
        }
    }
}




// --- Other Screens ---

@Composable
fun StudyPlanScreen(modifier: Modifier = Modifier, navigateTo: (Screen) -> Unit) {
    val items = listOf("自定义学习规划", "学霸经验")
    val icons = listOf(Icons.Filled.EditCalendar, Icons.Filled.Star)
    LazyVerticalGrid( columns = GridCells.Fixed(2), modifier = modifier.fillMaxSize().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp) ) {
        items(items.size) { index ->
            GridBlock( text = items[index], icon = icons[index],
                onClick = { when (items[index]) { "自定义学习规划" -> navigateTo(Screen.CustomStudyPlan); "学霸经验" -> navigateTo(Screen.TopStudentExperience) } }
            )
        }
    }
}

@Composable
fun CustomStudyPlanScreen( modifier: Modifier = Modifier, taskViewModel: TaskViewModel, navigateTo: (Screen) -> Unit ) {
    val subjects by taskViewModel.subjects.collectAsState()
    LazyVerticalGrid( columns = GridCells.Fixed(2), modifier = modifier.fillMaxSize().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp) ) {
        items(subjects) { subject -> GridBlock( text = subject.name, icon = subject.icon, onClick = { navigateTo(Screen.SubjectDetail(subject.name)) } ) }
    }
}

@Composable
fun SubjectDetailScreen( subjectName: String, taskViewModel: TaskViewModel, onKnowledgePointActionClick: (String) -> Unit, modifier: Modifier = Modifier ) {
    val subject = remember(subjectName) { taskViewModel.getSubjectByName(subjectName) }
    if (subject == null) { Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("错误：找不到科目 '$subjectName'") }; return }
    LazyColumn( modifier = modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(16.dp) ) {
        items(subject.chapters) { chapter -> ChapterItem( chapter = chapter, onKnowledgePointActionClick = onKnowledgePointActionClick ) }
    }
}

@Composable
fun ChapterItem( chapter: Chapter, onKnowledgePointActionClick: (String) -> Unit, modifier: Modifier = Modifier ) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text( text = chapter.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp) )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            chapter.knowledgePoints.forEach { point ->
                Row( modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween ) {
                    Text( text = "• ${point.text}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f).padding(end = 8.dp) )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        KnowledgePointActionButton( text = "听课", onClick = { onKnowledgePointActionClick("听课：${point.text}") } )
                        KnowledgePointActionButton( text = "刷题", onClick = { onKnowledgePointActionClick("刷题：${point.text}") } )
                        KnowledgePointActionButton( text = "背书", onClick = { onKnowledgePointActionClick("背书：${point.text}") } )
                    }
                }
            }
        }
        Divider(modifier = Modifier.padding(top = 16.dp))
    }
}

@Composable
fun KnowledgePointActionButton( text: String, onClick: () -> Unit, modifier: Modifier = Modifier ) {
    Button( onClick = onClick, modifier = modifier.height(32.dp), contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp), shape = RoundedCornerShape(8.dp) ) {
        Text(text = text, fontSize = 12.sp)
    }
}

@Composable
fun TopStudentExperienceScreen( taskViewModel: TaskViewModel, modifier: Modifier = Modifier ) {
    val imageUrls by taskViewModel.experienceImageUrls.collectAsState()
    LazyVerticalGrid( columns = GridCells.Fixed(2), modifier = modifier.fillMaxSize().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp) ) {
        items(imageUrls) { imageUrl -> ImageGridBlock( imageUrl = imageUrl, contentDescription = "学霸经验分享", onClick = { println("Clicked on image: $imageUrl") } ) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GridBlock(text: String, icon: ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card( onClick = onClick, modifier = modifier.aspectRatio(1f).clip(RoundedCornerShape(12.dp)), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant) ) {
        Column( modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center ) {
            Icon(imageVector = icon, contentDescription = text, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = text, fontWeight = FontWeight.Medium, fontSize = 16.sp, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageGridBlock( imageUrl: String, contentDescription: String?, onClick: () -> Unit, modifier: Modifier = Modifier ) {
    Card( onClick = onClick, modifier = modifier.aspectRatio(1f).clip(RoundedCornerShape(8.dp)), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) ) {
        Image( painter = rememberAsyncImagePainter( ImageRequest.Builder(LocalContext.current).data(data = imageUrl).crossfade(true).build() ), contentDescription = contentDescription, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop )
    }
}

@Composable
fun DiaryCalendarScreen( modifier: Modifier = Modifier, taskViewModel: TaskViewModel, navigateTo: (Screen) -> Unit ) {
    var currentMonthCalendar by remember { mutableStateOf(taskViewModel.getCurrentMonthCalendar()) }
    val daysInMonth = taskViewModel.getDaysInMonth(currentMonthCalendar)
    val firstOfMonthCalendar = taskViewModel.getFirstOfMonthCalendar(currentMonthCalendar)
    val firstDayOfWeekValue = taskViewModel.getDayOfWeek(firstOfMonthCalendar)
    val weekStartDayPreference = taskViewModel.getFirstDayOfWeekPreference()
    val startPadding = (firstDayOfWeekValue - weekStartDayPreference + 7) % 7
    val daysOfWeek = remember { taskViewModel.getShortDayNames() }
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Row( modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically ) {
            IconButton(onClick = { val prevMonth = currentMonthCalendar.clone() as Calendar; prevMonth.add(Calendar.MONTH, -1); currentMonthCalendar = prevMonth }) { Icon(Icons.Filled.ChevronLeft, contentDescription = "上个月") }
            Text( text = taskViewModel.formatMonthYear(currentMonthCalendar), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold )
            IconButton(onClick = { val nextMonth = currentMonthCalendar.clone() as Calendar; nextMonth.add(Calendar.MONTH, 1); currentMonthCalendar = nextMonth }) { Icon(Icons.Filled.ChevronRight, contentDescription = "下个月") }
        }
        Row( modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.SpaceAround ) { daysOfWeek.forEach { dayLabel -> Text( text = dayLabel, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center, modifier = Modifier.weight(1f) ) } }
        LazyVerticalGrid( columns = GridCells.Fixed(7), modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp) ) {
            items(startPadding) { Box(modifier = Modifier.aspectRatio(1f)) }
            items(daysInMonth) { dayIndex ->
                val dayOfMonth = dayIndex + 1
                val dateCalendar = taskViewModel.getCalendarForDay(currentMonthCalendar, dayOfMonth)
                val isToday = taskViewModel.isToday(dateCalendar)
                DayCell( day = dayOfMonth, date = dateCalendar, isToday = isToday, onClick = { selectedDateCal -> val dateString = taskViewModel.formatCalendarToIsoString(selectedDateCal); navigateTo(Screen.DiaryEntry(dateString)) } )
            }
        }
    }
}

@Composable
fun DayCell( day: Int, date: Calendar, isToday: Boolean, onClick: (Calendar) -> Unit, modifier: Modifier = Modifier ) {
    Box( modifier = modifier.aspectRatio(1f).clip(RoundedCornerShape(8.dp)).background( if (isToday) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color.Transparent ).border( width = if (isToday) 1.dp else 0.dp, color = if (isToday) MaterialTheme.colorScheme.primary else Color.Transparent, shape = RoundedCornerShape(8.dp) ).clickable { onClick(date) }, contentAlignment = Alignment.Center ) {
        Text( text = day.toString(), fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal, color = if (isToday) MaterialTheme.colorScheme.onPrimaryContainer else LocalContentColor.current )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryEntryScreen( date: Calendar, taskViewModel: TaskViewModel, onSave: (Calendar, String) -> Unit, modifier: Modifier = Modifier ) {
    val existingEntry = taskViewModel.getDiaryEntry(date) ?: ""
    var diaryText by remember { mutableStateOf(existingEntry) }
    val context = LocalContext.current
    Column( modifier = modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.SpaceBetween ) {
        OutlinedTextField( value = diaryText, onValueChange = { diaryText = it }, modifier = Modifier.fillMaxWidth().weight(1f), label = { Text("记录今天...") }, placeholder = { Text("今天发生了什么特别的事吗？") }, shape = RoundedCornerShape(8.dp) )
        Spacer(modifier = Modifier.height(16.dp))
        Button( onClick = { onSave(date, diaryText); Toast.makeText(context, "日记已保存", Toast.LENGTH_SHORT).show() }, modifier = Modifier.align(Alignment.End) ) { Text("保存") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitScreen( modifier: Modifier = Modifier, taskViewModel: TaskViewModel, navigateTo: (Screen) -> Unit ) {
    val habits by taskViewModel.predefinedHabits.collectAsState()
    val context = LocalContext.current
    LazyColumn( modifier = modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp) ) {
        item { Text( "点击挑选习惯", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp) ) }
        items(habits) { habit -> HabitListItem( habit = habit, onClick = { taskViewModel.addHabitTask(habit.name); Toast.makeText(context, "已添加习惯: ${habit.name}", Toast.LENGTH_SHORT).show(); navigateTo(Screen.TaskList) } ) }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun HabitListItem( habit: PredefinedHabit, onClick: () -> Unit, modifier: Modifier = Modifier ) {
    Card( modifier = modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant) ) {
        Row( modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                habit.icon?.let { Icon( imageVector = it, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp).padding(end = 16.dp) ) } ?: Spacer(modifier = Modifier.width(56.dp))
                Column {
                    Text( text = habit.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant )
                    habit.description?.let { Spacer(modifier = Modifier.height(4.dp)); Text( text = it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f) ) }
                }
            }
            Icon( imageVector = Icons.Filled.ChevronRight, contentDescription = "选择", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) )
        }
    }
}

@Composable
fun TaskItem(task: StudyTask, onStartClick: () -> Unit) {
    val progress = if (task.type == TaskType.TIMED_GOAL && task.targetMinutes > 0) { (task.completedMinutes.toFloat() / task.targetMinutes.toFloat()).coerceIn(0f, 1f) }
    else if (task.type == TaskType.HABIT && task.targetCount > 0) { (task.completedCount.toFloat() / task.targetCount.toFloat()).coerceIn(0f, 1f) }
    else { 0f }
    val timingModeText = when (task.timingMode) { TimingMode.COUNTDOWN -> "倒计时 - 目标"; TimingMode.FORWARD -> "正向计时 - 目标"; TimingMode.NONE -> "不计时 - 习惯" }
    val progressText = when (task.timingMode) { TimingMode.NONE -> "今日 ${task.completedCount}/${task.targetCount} 次"; else -> "${task.completedMinutes}/${task.targetMinutes} 分钟" }
    val isCompleted = task.isCompleted
    val contentColor = if (task.backgroundImageUrl != null) Color.White else LocalContentColor.current
    val progressColor = if (task.backgroundImageUrl != null) Color.White else MaterialTheme.colorScheme.primary
    val progressTrackColor = if (task.backgroundImageUrl != null) Color.White.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant
    val strikethroughColor = contentColor.copy(alpha = 0.7f)
    Card( modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min), shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) ) {
        Box {
            if (task.backgroundImageUrl != null) { Image( painter = rememberAsyncImagePainter( ImageRequest.Builder(LocalContext.current).data(data = task.backgroundImageUrl).crossfade(true).build() ), contentDescription = "任务背景", modifier = Modifier.matchParentSize(), contentScale = ContentScale.Crop ); Box(modifier = Modifier.matchParentSize().background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.5f), Color.Black.copy(alpha = 0.3f))))) }
            else { Box(modifier = Modifier.matchParentSize().background(task.backgroundColor)) }
            Row( modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(contentAlignment = Alignment.CenterStart) { Text( text = task.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = contentColor ); if (isCompleted) { Canvas(modifier = Modifier.matchParentSize()) { drawLine( color = strikethroughColor, start = Offset(0f, size.height / 2), end = Offset(size.width, size.height / 2), strokeWidth = 2.dp.toPx() ) } } }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(contentAlignment = Alignment.Center) { CircularProgressIndicator( progress = { progress }, modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = progressColor, trackColor = progressTrackColor ); Text( text = "${(progress * 100).toInt()}%", fontSize = 8.sp, color = contentColor ) }
                        Spacer(modifier = Modifier.width(8.dp)); Text( text = timingModeText, fontSize = 12.sp, color = contentColor.copy(alpha = 0.8f) ); Spacer(modifier = Modifier.width(8.dp)); Text( text = progressText, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = contentColor )
                    }
                    if (!isCompleted) { Text( text = "区间: ${task.timeInterval.displayName}", fontSize = 10.sp, color = contentColor.copy(alpha = 0.7f) ) }
                    if (task.type == TaskType.TIMED_GOAL && task.focusCount > 0) { Text( text = "今日已专注 ${task.focusCount} 次", fontSize = 10.sp, color = contentColor.copy(alpha = 0.7f) ) }
                }
                Button( onClick = onStartClick, enabled = !isCompleted, shape = CircleShape, contentPadding = PaddingValues(12.dp), colors = ButtonDefaults.buttonColors( containerColor = if (task.backgroundImageUrl != null) Color.White.copy(alpha = if (isCompleted) 0.5f else 0.9f) else MaterialTheme.colorScheme.primary, contentColor = if (task.backgroundImageUrl != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary, disabledContainerColor = if (task.backgroundImageUrl != null) Color.White.copy(alpha = 0.3f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), disabledContentColor = if (task.backgroundImageUrl != null) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f) ), modifier = Modifier.size(50.dp) ) {
                    if (task.timingMode == TimingMode.NONE) { Icon( imageVector = if (isCompleted) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked, contentDescription = if (isCompleted) "完成" else "打卡", modifier = Modifier.size(24.dp) ) }
                    else { Text( text = if (isCompleted) "完成" else "开始", fontSize = 14.sp ) }
                }
            }
        }
    }
}

@Composable
fun TimerScreen( timerState: TimerState, onPause: () -> Unit, onResume: () -> Unit, onStop: () -> Unit, modifier: Modifier = Modifier ) {
    val displayTimeMillis = when (timerState.timingMode) { TimingMode.COUNTDOWN -> timerState.remainingTimeMillis; TimingMode.FORWARD, TimingMode.NONE -> timerState.elapsedTimeMillis }
    val timeFormatted = formatTimeMillis(displayTimeMillis)
    Box(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize().background( Brush.verticalGradient(listOf(Color(0xFF003366), Color(0xFF001122)))) )
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)))
        Column( modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween ) {
            Text( text = "总之岁月漫长，然而值得等待", color = Color.White.copy(alpha = 0.8f), fontSize = 18.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 60.dp) )
            Column( horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f).wrapContentHeight(Alignment.CenterVertically) ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
                    CircularProgressIndicator( progress = { val target = timerState.targetMillis; val elapsed = timerState.elapsedTimeMillis; val remaining = timerState.remainingTimeMillis; when { timerState.timingMode == TimingMode.COUNTDOWN && target > 0 -> (target - remaining).toFloat() / target.toFloat(); timerState.timingMode == TimingMode.FORWARD && target > 0 -> (elapsed.toFloat() / target.toFloat()).coerceAtMost(1f); else -> 0f } }, modifier = Modifier.matchParentSize(), color = Color.White.copy(alpha = 0.8f), strokeWidth = 8.dp, strokeCap = StrokeCap.Round, trackColor = Color.White.copy(alpha = 0.2f) )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text( text = timerState.taskTitle, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center ); Spacer(modifier = Modifier.height(8.dp)); Text( text = timeFormatted, color = Color.White, fontSize = 72.sp, fontWeight = FontWeight.Light ); Spacer(modifier = Modifier.height(8.dp)); Text( text = when { timerState.isPaused -> "已暂停"; timerState.timingMode == TimingMode.COUNTDOWN -> "倒计时中"; timerState.timingMode == TimingMode.FORWARD -> "专注中"; else -> "计时未开始" }, color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp )
                    }
                }
            }
            Row( modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically ) {
                IconButton( onClick = { if (timerState.isPaused) onResume() else onPause() }, modifier = Modifier.size(70.dp).background(Color.White.copy(alpha = 0.2f), CircleShape) ) { Icon( imageVector = if (timerState.isPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause, contentDescription = if (timerState.isPaused) "重新开始" else "暂停", tint = Color.White, modifier = Modifier.size(40.dp) ) }
                IconButton( onClick = { /* TODO */ }, modifier = Modifier.size(70.dp).background(Color.White.copy(alpha = 0.2f), CircleShape) ) { Icon( imageVector = Icons.Filled.MusicNote, contentDescription = "音乐", tint = Color.White, modifier = Modifier.size(40.dp) ) }
                IconButton( onClick = onStop, modifier = Modifier.size(70.dp).background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(15.dp)) ) { Icon( imageVector = Icons.Filled.Stop, contentDescription = "停止", tint = Color.White, modifier = Modifier.size(40.dp) ) }
            }
        }
    }
}

@Composable
fun PauseDialog( pauseStartTimeMillis: Long, onDismiss: () -> Unit, onResume: () -> Unit ) {
    var currentPauseDuration by remember { mutableStateOf(0L) }
    LaunchedEffect(pauseStartTimeMillis) { if (pauseStartTimeMillis > 0) { while (true) { currentPauseDuration = System.currentTimeMillis() - pauseStartTimeMillis; delay(1000) } } else { currentPauseDuration = 0L } }
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp)) {
            Column( modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally ) {
                Text("已暂停", style = MaterialTheme.typography.headlineSmall); Spacer(modifier = Modifier.height(16.dp)); Text( text = "暂停时长: ${formatTimeMillis(currentPauseDuration)}", style = MaterialTheme.typography.bodyLarge ); Spacer(modifier = Modifier.height(24.dp)); Button(onClick = onResume) { Text("重新开始计时") }
            }
        }
    }
}

@Composable
fun StatisticsScreen( taskViewModel: TaskViewModel, modifier: Modifier = Modifier, navigateTo: (Screen) -> Unit ) {
    val statistics by taskViewModel.taskStatistics.collectAsState()
    LazyColumn( modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally ) {
        item { Text( text = "任务完成统计", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 24.dp) ) }
        item { StatisticCard( label = "今日完成", count = statistics.completedToday, timeMinutes = statistics.completedTodayTime, icon = Icons.Filled.LightMode, iconColor = Color(0xFFFFC107), onClick = { navigateTo(Screen.StatisticsDetail("today", "今日完成统计")) } ) }
        item { StatisticCard( label = "本周完成", count = statistics.completedThisWeek, timeMinutes = statistics.completedThisWeekTime, icon = Icons.Filled.DateRange, iconColor = Color(0xFF4CAF50), onClick = { navigateTo(Screen.StatisticsDetail("week", "本周完成统计")) } ) }
        item { StatisticCard( label = "本月完成", count = statistics.completedThisMonth, timeMinutes = statistics.completedThisMonthTime, icon = Icons.Filled.CalendarToday, iconColor = Color(0xFF2196F3), onClick = { navigateTo(Screen.StatisticsDetail("month", "本月完成统计")) } ) }
        item { StatisticCard( label = "本年完成", count = statistics.completedThisYear, timeMinutes = statistics.completedThisYearTime, icon = Icons.Filled.EventNote, iconColor = Color(0xFF9C27B0), onClick = { navigateTo(Screen.StatisticsDetail("year", "本年完成统计")) } ) }
        item { StatisticCard( label = "累计完成", count = statistics.completedTotal, timeMinutes = statistics.completedTotalTime, icon = Icons.Filled.CheckCircle, iconColor = MaterialTheme.colorScheme.tertiary, onClick = { navigateTo(Screen.StatisticsDetail("total", "累计完成统计")) } ) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticCard( label: String, count: Int, timeMinutes: Int, icon: ImageVector, iconColor: Color, onClick: () -> Unit ) {
    ElevatedCard( onClick = onClick, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), shape = RoundedCornerShape(12.dp) ) {
        Row( modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically ) {
            Icon( imageVector = icon, contentDescription = label, tint = iconColor, modifier = Modifier.size(36.dp) ); Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) { Text( text = label, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface ); if (timeMinutes > 0) { Text( text = "用时: ${formatDuration(timeMinutes)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant ) } }
            Spacer(modifier = Modifier.width(8.dp)); Text( text = "$count 项", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.End )
        }
    }
}

@Composable
fun StatisticsDetailScreen( periodType: String, periodLabel: String, taskViewModel: TaskViewModel, modifier: Modifier = Modifier ) {
    val allTasks by taskViewModel.sortedTasks.collectAsState()
    val now = remember { Calendar.getInstance() }
    val relevantTasks = remember(allTasks, periodType) {
        allTasks.filter { task -> task.isCompleted && task.completionTimestamp != null && task.completedMinutes > 0 }
            .filter { task ->
                val taskCalendar = Calendar.getInstance().apply { timeInMillis = task.completionTimestamp!! }
                when (periodType) { "today" -> taskViewModel.isSameDay(taskCalendar, now); "week" -> taskViewModel.isSameWeek(taskCalendar, now); "month" -> taskViewModel.isSameMonth(taskCalendar, now); "year" -> taskViewModel.isSameYear(taskCalendar, now); "total" -> true; else -> false }
            }
    }
    val pieChartData = remember(relevantTasks) {
        if (relevantTasks.isEmpty()) { emptyList() }
        else {
            val groupedByTitle = relevantTasks.groupBy { it.title }.mapValues { entry -> entry.value.sumOf { it.completedMinutes } }
            val colors = generateDistinctColors(groupedByTitle.size)
            groupedByTitle.entries.mapIndexedNotNull { index, entry -> if (entry.value > 0) { PieChartSlice( name = entry.key, value = entry.value.toFloat(), color = colors.getOrElse(index) { Color.LightGray } ) } else null }
        }
    }
    Column( modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally ) {
        if (pieChartData.isEmpty()) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text( text = "此时间段内没有已完成的事项数据可供显示。", style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp) ) } }
        else {
            Text( text = "$periodLabel 分布图", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 16.dp) )
            Column(horizontalAlignment = Alignment.CenterHorizontally) { PieChartComponent( slices = pieChartData, modifier = Modifier.fillMaxWidth(0.8f).aspectRatio(1f).padding(bottom = 24.dp) ); PieChartLegend( slices = pieChartData, modifier = Modifier.fillMaxWidth() ) }
        }
    }
}

@Composable
fun PieChartComponent( slices: List<PieChartSlice>, modifier: Modifier = Modifier, strokeWidth: Float = 4f ) {
    val totalValue = remember(slices) { slices.sumOf { it.value.toDouble() }.toFloat() }
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (totalValue == 0f) return@Canvas
            val chartRadius = min(size.width, size.height) / 2f
            val center = Offset(size.width / 2f, size.height / 2f)
            var startAngle = -90f
            slices.forEach { slice ->
                val sweepAngle = (slice.value / totalValue) * 360f
                drawArc( color = slice.color, startAngle = startAngle, sweepAngle = sweepAngle, useCenter = true, topLeft = Offset(center.x - chartRadius, center.y - chartRadius), size = Size(chartRadius * 2, chartRadius * 2) )
                if (strokeWidth > 0) { drawArc( color = Color.White, startAngle = startAngle, sweepAngle = sweepAngle, useCenter = true, style = Stroke(width = strokeWidth), topLeft = Offset(center.x - chartRadius, center.y - chartRadius), size = Size(chartRadius * 2, chartRadius * 2) ) }
                startAngle += sweepAngle
            }
        }
    }
}

@Composable
fun PieChartLegend( slices: List<PieChartSlice>, modifier: Modifier = Modifier ) {
    LazyColumn( modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp) ) {
        items(slices) { slice ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box( modifier = Modifier.size(16.dp).background(slice.color, CircleShape) ); Spacer(modifier = Modifier.width(8.dp)); Text( text = "${slice.name} (${formatDuration(slice.value.toInt())})", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface )
            }
        }
    }
}

// --- MODIFIED: Practice Question Screens ---

@Composable
fun PracticeSubjectsScreen(modifier: Modifier = Modifier, taskViewModel: TaskViewModel, navigateTo: (Screen) -> Unit) {
    val subjects by taskViewModel.subjects.collectAsState()
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(subjects) { subject ->
            GridBlock(
                text = subject.name,
                icon = subject.icon,
                onClick = { navigateTo(Screen.PracticeModeSelection(subject.name)) } // MODIFIED
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeKnowledgePointItem(knowledgePoint: KnowledgePoint, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "• ${knowledgePoint.text}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "选择题目类型",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)


@Composable
fun QuizSessionScreen(
    subjectName: String,
    mode: String,
    chapterId: String?, // NEW: Accept the optional chapterId
    taskViewModel: TaskViewModel,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val subject = remember(subjectName) { taskViewModel.getSubjectByName(subjectName) }
    val allQuestions by taskViewModel.practiceQuestions.collectAsState()
    val questionStates by taskViewModel.practiceQuestionStates.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val sessionQuestions = remember(allQuestions, questionStates, subject, mode, chapterId) {
        // First, get all questions for the subject
        val subjectKps = subject?.chapters?.flatMap { it.knowledgePoints }?.map { it.id }?.toSet() ?: emptySet()
        val subjectQuestions = allQuestions.filter { subjectKps.contains(it.knowledgePointId) }

        // Then, if a chapterId is provided, filter down to just that chapter
        val questionsToPractice = if (chapterId != null) {
            val chapterKps = subject?.chapters
                ?.find { it.id == chapterId }
                ?.knowledgePoints?.map { it.id }?.toSet() ?: emptySet()
            subjectQuestions.filter { chapterKps.contains(it.knowledgePointId) }
        } else {
            subjectQuestions
        }

        // Final sorting/shuffling based on mode
        when (mode) {
            "review" -> questionsToPractice.filter { taskViewModel.isPracticeQuestionDue(it.id) }.shuffled()
            "learn" -> questionsToPractice.sortedBy { it.id }
            "hybrid" -> {
                val (due, notDue) = questionsToPractice.partition { taskViewModel.isPracticeQuestionDue(it.id) }
                due.shuffled() + notDue.shuffled()
            }
            else -> emptyList()
        }
    }

    var currentQuestionIndex by remember { mutableStateOf(0) }
    val currentQuestion = sessionQuestions.getOrNull(currentQuestionIndex)

    var isSubmitted by remember { mutableStateOf(false) }
    var selectedOptionId by remember { mutableStateOf<String?>(null) }
    var shortAnswerText by remember { mutableStateOf("") }

    var showAiHint by remember { mutableStateOf(false) }
    var aiHintText by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    fun handleNextQuestion() {
        if (currentQuestionIndex < sessionQuestions.size - 1) {
            currentQuestionIndex++
            isSubmitted = false
            selectedOptionId = null
            shortAnswerText = ""
            aiHintText = null
        } else {
            Toast.makeText(context, "本轮练习已完成！", Toast.LENGTH_SHORT).show()
            onFinish()
        }
    }

    fun handleSubmit() {
        if (currentQuestion == null) return
        val isCorrect = when (currentQuestion) {
            is MultipleChoiceQuestion -> selectedOptionId == currentQuestion.correctOptionId
            is ShortAnswerQuestion -> true // Short answer is always "correct" for review purposes, user self-evaluates.
        }
        taskViewModel.updatePracticeQuestionState(currentQuestion.id, isCorrect)
        isSubmitted = true
    }

    if (sessionQuestions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
            Text( "此模式下没有题目。\n（在复习模式下，可能今天没有到期的题目）", textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant )
        }
        return
    }

    if (currentQuestion == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("错误：题目加载失败") }
        return
    }

    // AI Hint Dialog
    if (showAiHint && aiHintText != null) {
        AlertDialog(
            onDismissRequest = { showAiHint = false },
            title = { Text("AI 提示") },
            text = { Text(aiHintText!!) },
            confirmButton = { TextButton(onClick = { showAiHint = false }) { Text("好的") } }
        )
    }

    Column(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp)
    ) {
        // Progress Header
        Column {
            LinearProgressIndicator(
                progress = { (currentQuestionIndex + 1).toFloat() / sessionQuestions.size.toFloat() },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape)
            )
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text( text = subject?.name ?: "刷题", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Text( text = "进度: ${currentQuestionIndex + 1} / ${sessionQuestions.size}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant )
            }
        }
        Spacer(Modifier.height(16.dp))

        // Main content area
        Column(modifier = Modifier.weight(1f).verticalScroll(scrollState)) {
            // Question Area
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("(${currentQuestion.type.displayName})", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Text(currentQuestion.questionText, style = MaterialTheme.typography.headlineSmall)
                }
            }
            Spacer(Modifier.height(16.dp))

            // AI Hint Button
            if (!isSubmitted) {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            aiHintText = taskViewModel.getAiGeneratedHint(currentQuestion)
                            showAiHint = true
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(Icons.Default.Lightbulb, contentDescription = "AI提示", modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("请求AI提示")
                }
                Spacer(Modifier.height(8.dp))
            }


            // Answer Area (same as your original code, no changes needed here)
            when (currentQuestion) {
                is MultipleChoiceQuestion -> {
                    currentQuestion.options.forEach { option ->
                        val isCorrect = option.id == currentQuestion.correctOptionId
                        val isSelected = option.id == selectedOptionId
                        val backgroundColor = when {
                            !isSubmitted -> MaterialTheme.colorScheme.surfaceVariant
                            isCorrect -> Color(0xFFC8E6C9) // Green for correct
                            isSelected && !isCorrect -> Color(0xFFFFCDD2) // Red for wrong selected
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                        val borderColor = when {
                            !isSubmitted && isSelected -> MaterialTheme.colorScheme.primary
                            isSubmitted && isCorrect -> Color(0xFF388E3C) // Darker Green border
                            isSubmitted && isSelected && !isCorrect -> Color(0xFFD32F2F) // Darker Red border
                            else -> Color.Transparent
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                                .clickable(enabled = !isSubmitted) { selectedOptionId = option.id },
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = backgroundColor)
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedOptionId = option.id },
                                    enabled = !isSubmitted
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(option.text, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                }
                is ShortAnswerQuestion -> {
                    OutlinedTextField(
                        value = shortAnswerText,
                        onValueChange = { shortAnswerText = it },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                        label = { Text("在此输入您的答案") },
                        readOnly = isSubmitted,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Analysis/Feedback Area
            AnimatedVisibility(visible = isSubmitted) {
                var rephrasedAnalysis by remember { mutableStateOf<String?>(null) }

                Column {
                    val isWrong = (currentQuestion is MultipleChoiceQuestion && selectedOptionId != currentQuestion.correctOptionId)

                    if (currentQuestion is ShortAnswerQuestion) {
                        Text("参考答案:", style = MaterialTheme.typography.titleSmall)
                        Text(currentQuestion.referenceAnswer, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(16.dp))
                    }
                    Text("解析:", style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(8.dp))
                    Text(rephrasedAnalysis ?: currentQuestion.analysis, style = MaterialTheme.typography.bodyLarge, lineHeight = 22.sp)

                    // NEW: AI rephrase button appears if answer is wrong
                    if (isWrong) {
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    rephrasedAnalysis = taskViewModel.getAiRephrasedAnalysis(currentQuestion.analysis)
                                }
                            },
                            enabled = rephrasedAnalysis == null // Disable after clicking once
                        ) {
                            Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("让AI换一种方式解释")
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Action Button
        Button(
            onClick = {
                if (isSubmitted) {
                    handleNextQuestion()
                } else {
                    handleSubmit()
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = isSubmitted || (selectedOptionId != null || shortAnswerText.isNotBlank())
        ) {
            Text(
                when {
                    !isSubmitted -> "提交答案"
                    currentQuestionIndex < sessionQuestions.size - 1 -> "下一题"
                    else -> "完成练习"
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateQuestionScreen(
    knowledgePointId: String,
    taskViewModel: TaskViewModel,
    onQuestionCreated: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var questionText by remember { mutableStateOf("") }
    var analysisText by remember { mutableStateOf("") }
    var selectedQuestionType by remember { mutableStateOf(QuestionType.MULTIPLE_CHOICE) }

    // Multiple Choice State
    var options by remember { mutableStateOf(listOf("", "")) }
    var correctOptionIndex by remember { mutableStateOf(0) }

    // Short Answer State
    var referenceAnswer by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // Question Type Selector
        Text("选择题目类型", style = MaterialTheme.typography.titleMedium)
        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            QuestionType.values().forEach { type ->
                FilterChip(
                    selected = selectedQuestionType == type,
                    onClick = { selectedQuestionType = type },
                    label = { Text(type.displayName) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
        Spacer(Modifier.height(16.dp))

        // Common Fields
        OutlinedTextField(
            value = questionText,
            onValueChange = { questionText = it },
            label = { Text("题干") },
            placeholder = { Text("在此输入题目的主要内容...") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp)
        )
        Spacer(Modifier.height(16.dp))

        // Type-specific fields
        when (selectedQuestionType) {
            QuestionType.MULTIPLE_CHOICE -> {
                Text("选项（至少2个）", style = MaterialTheme.typography.titleMedium)
                options.forEachIndexed { index, optionText ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = correctOptionIndex == index,
                            onClick = { correctOptionIndex = index }
                        )
                        OutlinedTextField(
                            value = optionText,
                            onValueChange = { newText ->
                                options = options.toMutableList().also { it[index] = newText }
                            },
                            label = { Text("选项 ${index + 1}") },
                            modifier = Modifier.weight(1f)
                        )
                        if (options.size > 2) {
                            IconButton(onClick = {
                                options = options.toMutableList().also { it.removeAt(index) }
                                if (correctOptionIndex >= options.size) {
                                    correctOptionIndex = options.size - 1
                                }
                            }) {
                                Icon(Icons.Default.RemoveCircleOutline, contentDescription = "删除选项")
                            }
                        }
                    }
                }
                TextButton(onClick = { options = options + "" }) {
                    Text("添加选项")
                }
            }
            QuestionType.SHORT_ANSWER -> {
                OutlinedTextField(
                    value = referenceAnswer,
                    onValueChange = { referenceAnswer = it },
                    label = { Text("参考答案") },
                    placeholder = { Text("在此输入简答题的参考答案...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp)
                )
            }
        }
        Spacer(Modifier.height(16.dp))

        // Analysis Field
        OutlinedTextField(
            value = analysisText,
            onValueChange = { analysisText = it },
            label = { Text("解析") },
            placeholder = { Text("在此输入题目的详细解析...") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp)
        )
        Spacer(Modifier.height(24.dp))

        // Save Button
        Button(
            onClick = {
                if (questionText.isBlank() || analysisText.isBlank()) {
                    Toast.makeText(context, "题干和解析不能为空", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val newQuestion = when (selectedQuestionType) {
                    QuestionType.MULTIPLE_CHOICE -> {
                        val finalOptions = options
                            .map { it.trim() }
                            .filter { it.isNotEmpty() }
                            .map { MultipleChoiceOption(text = it) }

                        if (finalOptions.size < 2) {
                            Toast.makeText(context, "选择题至少需要2个有效选项", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (correctOptionIndex >= finalOptions.size) {
                            Toast.makeText(context, "请为有效选项选择一个正确答案", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        MultipleChoiceQuestion(
                            knowledgePointId = knowledgePointId,
                            questionText = questionText.trim(),
                            analysis = analysisText.trim(),
                            options = finalOptions,
                            correctOptionId = finalOptions[correctOptionIndex].id
                        )
                    }
                    QuestionType.SHORT_ANSWER -> {
                        if (referenceAnswer.isBlank()) {
                            Toast.makeText(context, "参考答案不能为空", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        ShortAnswerQuestion(
                            knowledgePointId = knowledgePointId,
                            questionText = questionText.trim(),
                            analysis = analysisText.trim(),
                            referenceAnswer = referenceAnswer.trim()
                        )
                    }
                }
                taskViewModel.addPracticeQuestion(newQuestion)
                Toast.makeText(context, "题目已成功创建！", Toast.LENGTH_SHORT).show()
                onQuestionCreated()
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("保存题目")
        }
    }
}


// --- Helper Functions & Theme ---

fun generateDistinctColors(count: Int): List<Color> {
    val colors = mutableListOf<Color>()
    val random = Random(System.currentTimeMillis())
    val baseColors = listOf( Color(0xFFF44336), Color(0xFFE91E63), Color(0xFF9C27B0), Color(0xFF673AB7), Color(0xFF3F51B5), Color(0xFF2196F3), Color(0xFF03A9F4), Color(0xFF00BCD4), Color(0xFF009688), Color(0xFF4CAF50), Color(0xFF8BC34A), Color(0xFFCDDC39), Color(0xFFFFEB3B), Color(0xFFFFC107), Color(0xFFFF9800), Color(0xFFFF5722), Color(0xFF795548), Color(0xFF9E9E9E), Color(0xFF607D8B) )
    for (i in 0 until count) { if (i < baseColors.size) { colors.add(baseColors[i]) } else { colors.add( Color( red = random.nextInt(100, 256), green = random.nextInt(100, 256), blue = random.nextInt(100, 256) ) ) } }
    return colors.shuffled(random)
}

fun formatDuration(totalMinutes: Int): String {
    if (totalMinutes <= 0) return "0 分钟"
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return when { hours > 0 && minutes > 0 -> "$hours 小时 $minutes 分钟"; hours > 0 && minutes == 0 -> "$hours 小时"; else -> "$minutes 分钟" }
}

fun formatTimeMillis(millis: Long): String {
    val totalSeconds = TimeUnit.MILLISECONDS.toSeconds(millis).coerceAtLeast(0)
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) { String.format("%02d:%02d:%02d", hours, minutes, seconds) } else { String.format("%02d:%02d", minutes, seconds) }
}
@Composable
fun ProfileScreen(taskViewModel: TaskViewModel, navigateTo: (Screen) -> Unit, modifier: Modifier = Modifier) {
    val userProfile by taskViewModel.userProfile.collectAsState()
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 用户信息头
        item {
            ProfileHeader(
                userProfile = userProfile,
                onEditClick = { taskViewModel.openEditProfileDialog() } // 修改
            )
        }

        item { Spacer(Modifier.height(16.dp)) }

        // 通用设置
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column {
                    ProfileMenuItem(
                        icon = Icons.Outlined.Security,
                        title = "账号与安全",
                        onClick = { navigateTo(Screen.Settings) } // 修改
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileMenuItem(
                        icon = Icons.Outlined.Notifications,
                        title = "通知设置",
                        onClick = { navigateTo(Screen.Settings) } // 修改
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileMenuItem(
                        icon = Icons.Outlined.Palette,
                        title = "外观",
                        onClick = { navigateTo(Screen.Settings) } // 修改
                    )
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }

        // 其他
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column {
                    ProfileMenuItem(
                        icon = Icons.Outlined.HelpOutline,
                        title = "帮助与反馈",
                        onClick = { Toast.makeText(context, "功能待实现", Toast.LENGTH_SHORT).show() } // 保留
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileMenuItem(
                        icon = Icons.Outlined.Info,
                        title = "关于我们",
                        onClick = { navigateTo(Screen.About) } // 修改
                    )
                }
            }
        }

        item { Spacer(Modifier.height(24.dp)) }

        // 退出登录按钮
        item {
            Button(
                onClick = { taskViewModel.openLogoutConfirmDialog() }, // 修改
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Icon(
                    Icons.Outlined.ExitToApp,
                    contentDescription = "退出登录",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(Modifier.width(8.dp))
                Text("退出登录", color = MaterialTheme.colorScheme.onErrorContainer)
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
fun ProfileHeader(userProfile: UserProfile, onEditClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current).data(data = userProfile.avatarUrl)
                    .crossfade(true).build()
            ),
            contentDescription = "用户头像",
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
        )

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = userProfile.nickname,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = userProfile.studySignature,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(onClick = onEditClick) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = "编辑个人资料"
            )
        }
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline
        )
    }
}
@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    // 为了简化，我们暂时复用 PlaceholderScreen
    PlaceholderScreen(title = "设置")
}

@Composable
fun AboutScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 在实际应用中，您可能会使用 R.drawable.app_icon
        Icon(
            imageVector = Icons.Filled.School,
            contentDescription = "App Icon",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "学习时钟",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "版本 1.0.0",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun EditProfileDialog(
    userProfile: UserProfile,
    onDismiss: () -> Unit,
    onSave: (newNickname: String, newSignature: String) -> Unit
) {
    var nickname by remember { mutableStateOf(userProfile.nickname) }
    var signature by remember { mutableStateOf(userProfile.studySignature) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("编辑个人资料", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    label = { Text("昵称") }
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = signature,
                    onValueChange = { signature = it },
                    label = { Text("学习签名") }
                )
                Spacer(Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("取消") }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { onSave(nickname, signature) }) { Text("保存") }
                }
            }
        }
    }
}

@Composable
fun LogoutConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("退出登录") },
        text = { Text("您确定要退出当前账号吗？") },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text("确定退出")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
fun PlaceholderScreen(title: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium)
    }
}

@Composable
fun StudyTimerAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF007AFF), onPrimary = Color.White, primaryContainer = Color(0xFFD1E5FF), onPrimaryContainer = Color(0xFF001D36),
            secondary = Color(0xFF535F70), onSecondary = Color.White, secondaryContainer = Color(0xFFD7E3F7), onSecondaryContainer = Color(0xFF101C2B),
            tertiary = Color(0xFF6B5778), onTertiary = Color.White, tertiaryContainer = Color(0xFFF2DAFF), onTertiaryContainer = Color(0xFF251431),
            error = Color(0xFFBA1A1A), errorContainer = Color(0xFFFFDAD6), onError = Color.White, onErrorContainer = Color(0xFF410002),
            background = Color(0xFFFBFBFF), // A slightly off-white background
            onBackground = Color(0xFF1A1C1E),
            surface = Color(0xFFFDFBFF), // Surface for cards
            onSurface = Color(0xFF1A1C1E),
            surfaceVariant = Color(0xFFE0E2EC), // Background for things like chips, text fields
            onSurfaceVariant = Color(0xFF43474E),
            outline = Color(0xFF74777F), inverseOnSurface = Color(0xFFF1F0F4), inverseSurface = Color(0xFF2F3033), inversePrimary = Color(0xFFA0CAFF),
            surfaceTint = Color(0xFF0061A4), outlineVariant = Color(0xFFC3C6CF), scrim = Color.Black,
        ),
        typography = Typography,
        content = content
    )
}

val Typography = Typography(
    bodyLarge = TextStyle( fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.5.sp ),
    titleLarge = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 22.sp, lineHeight = 28.sp, letterSpacing = 0.sp),
    titleMedium = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium, fontSize = 18.sp, lineHeight = 24.sp, letterSpacing = 0.15.sp),
    bodySmall = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.4.sp),
    headlineSmall = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = 32.sp, letterSpacing = 0.sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 28.sp, lineHeight = 36.sp, letterSpacing = 0.sp)
)