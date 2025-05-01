from typing import Dict, Any, Optional
from pydantic import BaseModel, Field


class Mindmap(BaseModel):
    id: str = Field(..., description="Unique identifier for the mindmap")
    content: str = Field(..., description="HTML content representing the mindmap")
    summary: Optional[str] = Field(None, description="Optional summary of the mindmap")

    class Config:
        json_schema_extra = {
            "example": {
                "id": "mindmap789",
                "content": "<div class='mindmap'><div class='root'>Central Idea</div><div class='branch'>Topic A</div></div>",
                "summary": "HTML representation of a simple mindmap.",
            }
        }