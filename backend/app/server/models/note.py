from pydantic import BaseModel, Field
from enum import Enum

class NodeType(str, Enum):
    TEXT = "text"
    LINK = "link"
    FILE = "file"

class Node(BaseModel):
    id: str = Field(..., description="Unique identifier for the node")
    input: str = Field(..., description="Content of the node")
    type: NodeType = Field(..., description="Type of the node (e.g., text, image)")
    user_id: str = Field(..., description="Identifier of the user who created the node")

    class Config:
        json_schema_extra = {
            "example": {
                "id": "node101",
                "input": "Hello World!",
                "type": "text",
                "user_id": "user123",
            }
        }