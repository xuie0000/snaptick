package com.vishal2376.snaptick.widget.di

import com.vishal2376.snaptick.data.repositories.TaskRepository
import com.vishal2376.snaptick.util.SettingsStore
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt entry point for code paths that don't run inside an `@AndroidEntryPoint`
 * scope - notably Glance `ActionCallback` instances. Resolved at call time
 * via `EntryPointAccessors.fromApplication(context)`.
 *
 * The widget needs full `TaskRepository` access so completion toggles from
 * the widget go through the same path as the home screen: per-date completion
 * for repeat templates, alarm cancel/reschedule for one-offs. SettingsStore
 * is exposed so widget actions can refresh the widget state inline without
 * a WorkManager hop.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
	fun taskRepository(): TaskRepository
	fun settingsStore(): SettingsStore
}
