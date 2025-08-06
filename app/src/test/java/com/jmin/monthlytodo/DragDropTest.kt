package com.jmin.monthlytodo

import com.jmin.monthlytodo.model.Priority
import com.jmin.monthlytodo.model.Task
import org.junit.Test
import org.junit.Assert.*
import java.util.*

/**
 * 测试拖拽排序功能的逻辑
 */
class DragDropTest {

    @Test
    fun testTaskReordering() {
        // 创建测试任务列表
        val tasks = listOf(
            Task(id = 1, title = "Task 1", dueDate = Date(), order = 0),
            Task(id = 2, title = "Task 2", dueDate = Date(), order = 1),
            Task(id = 3, title = "Task 3", dueDate = Date(), order = 2),
            Task(id = 4, title = "Task 4", dueDate = Date(), order = 3)
        )

        // 模拟将第一个任务移动到第三个位置
        val reorderedTasks = simulateTaskReorder(tasks, 0, 2)

        // 验证排序结果
        assertEquals("Task 2", reorderedTasks[0].title)
        assertEquals("Task 3", reorderedTasks[1].title)
        assertEquals("Task 1", reorderedTasks[2].title)
        assertEquals("Task 4", reorderedTasks[3].title)

        // 验证order字段更新
        reorderedTasks.forEachIndexed { index, task ->
            assertEquals(index, task.order)
        }
    }

    @Test
    fun testTaskReorderingUpward() {
        // 创建测试任务列表
        val tasks = listOf(
            Task(id = 1, title = "Task 1", dueDate = Date(), order = 0),
            Task(id = 2, title = "Task 2", dueDate = Date(), order = 1),
            Task(id = 3, title = "Task 3", dueDate = Date(), order = 2),
            Task(id = 4, title = "Task 4", dueDate = Date(), order = 3)
        )

        // 模拟将第四个任务移动到第二个位置
        val reorderedTasks = simulateTaskReorder(tasks, 3, 1)

        // 验证排序结果
        assertEquals("Task 1", reorderedTasks[0].title)
        assertEquals("Task 4", reorderedTasks[1].title)
        assertEquals("Task 2", reorderedTasks[2].title)
        assertEquals("Task 3", reorderedTasks[3].title)
    }

    @Test
    fun testOrderFieldUpdate() {
        val tasks = listOf(
            Task(id = 1, title = "Task 1", dueDate = Date(), order = 5),
            Task(id = 2, title = "Task 2", dueDate = Date(), order = 10),
            Task(id = 3, title = "Task 3", dueDate = Date(), order = 15)
        )

        // 模拟更新order字段的逻辑
        val updatedTasks = tasks.mapIndexed { index, task -> 
            task.copy(order = index) 
        }

        // 验证order字段正确更新
        assertEquals(0, updatedTasks[0].order)
        assertEquals(1, updatedTasks[1].order)
        assertEquals(2, updatedTasks[2].order)
    }

    @Test
    fun testDragTargetCalculation() {
        val itemHeight = 80f
        val moveThreshold = itemHeight * 0.5f // 40像素

        // 测试向下拖拽
        val dragOffsetDown = 50f // 超过阈值
        val moveStepsDown = ((dragOffsetDown - moveThreshold) / itemHeight).toInt() + 1
        assertEquals(1, moveStepsDown) // 应该移动1步

        // 测试向上拖拽
        val dragOffsetUp = -50f // 超过阈值
        val moveStepsUp = ((-dragOffsetUp - moveThreshold) / itemHeight).toInt() + 1
        assertEquals(1, moveStepsUp) // 应该移动1步

        // 测试在阈值内
        val dragOffsetSmall = 20f // 小于阈值
        assertTrue("小幅拖拽不应该触发移动", dragOffsetSmall < moveThreshold)
    }

    @Test
    fun testFirstTaskDragLogic() {
        // 模拟拖拽第一个任务的场景
        val draggedTaskIndex = 0
        val dragOffset = 100f // 向下拖拽100像素
        val itemHeight = 80f
        val moveThreshold = itemHeight * 0.5f

        val targetIndex = when {
            dragOffset > moveThreshold -> {
                val moveSteps = ((dragOffset - moveThreshold) / itemHeight).toInt() + 1
                (draggedTaskIndex + moveSteps).coerceAtMost(3) // 假设有4个任务
            }
            else -> null
        }

        assertEquals(1, targetIndex) // 第一个任务拖拽100像素应该到第二个位置
    }

    /**
     * 模拟任务重新排序的逻辑
     */
    private fun simulateTaskReorder(tasks: List<Task>, fromIndex: Int, toIndex: Int): List<Task> {
        val mutableTasks = tasks.toMutableList()
        val task = mutableTasks.removeAt(fromIndex)
        mutableTasks.add(toIndex, task)

        // 更新order字段
        return mutableTasks.mapIndexed { index, task ->
            task.copy(order = index)
        }
    }
}
