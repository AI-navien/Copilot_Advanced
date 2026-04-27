## F1 키를 누르고, "MCP: Open Workspace Folder MCP Configuration" 를 입력 후 아래 내용을 붙여넣기
{
  "servers": {
    "github": {
      "command": "docker",
      "args": [
        "run",
        "-i",
        "--rm",
        "-e",
        "GITHUB_PERSONAL_ACCESS_TOKEN",
        "ghcr.io/github/github-mcp-server"
      ],
      "env": {
        "GITHUB_PERSONAL_ACCESS_TOKEN": ""
      }
    },
    "quibbler": {
      "command": "quibbler",
      "args": [
        "mcp"
      ],
      "env": {
        "ANTHROPIC_API_KEY": ""
      }
    }
  }
}