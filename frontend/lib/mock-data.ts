// Generate random number between min and max
const randomInt = (min: number, max: number) => {
  return Math.floor(Math.random() * (max - min + 1) + min)
}

// Generate mock data for the dashboard
export const generateMockData = () => {
  // Types of content
  const contentTypes = ["Link", "Text", "Audio", "Image", "File"]

  // Generate data for notes by hour
  const notesByHour = Array.from({ length: 24 }, (_, i) => ({
    name: `${i}:00`,
    value: randomInt(5, 50),
  }))

  // Generate data for notes by day
  const days = ["Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"]
  const notesByDay = days.map((day) => ({
    name: day,
    value: randomInt(20, 100),
  }))

  // Generate data for notes by type
  const notesByType = contentTypes.map((type) => ({
    name: type,
    value: randomInt(30, 150),
  }))

  // Calculate total notes
  const totalNotes = notesByType.reduce((sum, item) => sum + item.value, 0)

  // Generate data for quiz by type
  const quizByType = contentTypes.map((type) => ({
    name: type,
    value: randomInt(10, 80),
  }))

  // Calculate total quiz
  const totalQuiz = quizByType.reduce((sum, item) => sum + item.value, 0)

  // Generate data for flashcard by type
  const flashcardByType = contentTypes.map((type) => ({
    name: type,
    value: randomInt(15, 90),
  }))

  // Calculate total flashcard
  const totalFlashcard = flashcardByType.reduce((sum, item) => sum + item.value, 0)

  // Generate data for mindmap by type
  const mindmapByType = contentTypes.map((type) => ({
    name: type,
    value: randomInt(5, 60),
  }))

  // Calculate total mindmap
  const totalMindmap = mindmapByType.reduce((sum, item) => sum + item.value, 0)

  // Generate user data
  const userNames = [
    "Nguyễn Văn A",
    "Trần Thị B",
    "Lê Văn C",
    "Phạm Thị D",
    "Hoàng Văn E",
    "Đỗ Thị F",
    "Vũ Văn G",
    "Ngô Thị H",
    "Đặng Văn I",
    "Bùi Thị K",
  ]

  const notesPerUser = userNames.map((name) => {
    const notes = randomInt(5, 100)
    return {
      name,
      notes,
    }
  })

  // Calculate user statistics
  const maxNotes = Math.max(...notesPerUser.map((user) => user.notes))
  const minNotes = Math.min(...notesPerUser.map((user) => user.notes))
  const avgNotes = Math.round(notesPerUser.reduce((sum, user) => sum + user.notes, 0) / notesPerUser.length)

  return {
    notes: {
      total: totalNotes,
      byHour: notesByHour,
      byDay: notesByDay,
      byType: notesByType,
    },
    quiz: {
      total: totalQuiz,
      byType: quizByType,
    },
    flashcard: {
      total: totalFlashcard,
      byType: flashcardByType,
    },
    mindmap: {
      total: totalMindmap,
      byType: mindmapByType,
    },
    users: {
      total: userNames.length,
      notesPerUser,
      maxNotes,
      minNotes,
      avgNotes,
    },
  }
}
