package com.example.final_exam;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private IdiomDao idiomDao;
    private String currentLevel; // 当前难度变量
    private List<IdiomModel> currentIdioms;
    private int currentIdiomIndex = 0;
    private int hintCount = 3;
    private List<CharacterComponent> components;

    private GridLayout componentsGrid;
    private GridLayout targetGrid;
    private TextView idiomExplanation;
    private Button nextButton;
    private Button backButton;
    private Button hintButton;
    private TextView fullIdiomDisplay;
    private TextView timerTextView; // 计时器显示

    // 计时器相关变量
    private long startTime = 0;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        idiomDao = new IdiomDao(this);
        componentsGrid = findViewById(R.id.components_grid);
        targetGrid = findViewById(R.id.target_grid);
        idiomExplanation = findViewById(R.id.idiom_explanation);
        nextButton = findViewById(R.id.next_button);
        backButton = findViewById(R.id.back_button);
        hintButton = findViewById(R.id.hint_button);
        fullIdiomDisplay = findViewById(R.id.full_idiom_display);
        timerTextView = findViewById(R.id.timer_textview); // 初始化计时器显示

        // 获取难度参数并保存
        currentLevel = getIntent().getStringExtra("level");
        if (currentLevel == null) {
            currentLevel = "primary";
        }

        // 加载指定难度的成语
        loadIdioms(currentLevel);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNextIdiom();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 处理返回操作，启动选择难度界面
                Intent intent = new Intent(MainActivity.this, DifficultySelectionActivity.class);
                startActivity(intent);
                finish();
            }
        });

        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hintCount > 0) {
                    showHint();
                    hintCount--;
                    updateHintCounter();
                    if (hintCount == 0) {
                        hintButton.setEnabled(false);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "没有剩余提示次数了！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        updateHintCounter();

        // 启动计时器
        startTimer();
    }

    // 启动计时器
    private void startTimer() {
        startTime = System.currentTimeMillis();
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                long millis = System.currentTimeMillis() - startTime;
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;

                // 格式化时间显示
                String time = String.format("已用时间: %02d:%02d", minutes, seconds);
                timerTextView.setText(time);

                // 每秒更新一次
                timerHandler.postDelayed(this, 1000);
            }
        };
        timerHandler.postDelayed(timerRunnable, 0);
    }

    // 停止计时器
    private void stopTimer() {
        timerHandler.removeCallbacks(timerRunnable);
    }

    private void loadIdioms(String level) {
        currentIdioms = idiomDao.getRandomIdiomsByLevel(level, 5);
        if (!currentIdioms.isEmpty()) {
            loadCurrentIdiom();
        }
    }

    private void loadCurrentIdiom() {
        IdiomModel currentIdiom = currentIdioms.get(currentIdiomIndex);
        idiomExplanation.setText(currentIdiom.getExplanation());
        fullIdiomDisplay.setVisibility(View.GONE); // 加载新成语时隐藏显示框

        // 解析成语为部件
        parseIdiomToComponents(currentIdiom);

        // 显示部件和目标位置
        displayComponents();
        displayTargetPositions(currentIdiom.getIdiom());
    }

    private void parseIdiomToComponents(IdiomModel idiomModel) {
        components = new ArrayList<>();
        Random random = new Random();

        String idiom = idiomModel.getIdiom();
        for (char c : idiom.toCharArray()) {
            String character = String.valueOf(c);
            components.add(new CharacterComponent(character, character));
        }

        // 添加干扰部件
        String interferingPartsStr = idiomModel.getInterferingParts();
        String[] interferingPartsArray = interferingPartsStr.split("、");
        List<String> interferingPartsList = new ArrayList<>(Arrays.asList(interferingPartsArray));
        int interferingNumber = Math.max(2, 8 - components.size());

        for (int i = 0; i < interferingNumber; i++) {
            if (interferingPartsList.isEmpty()) {
                break; // 如果干扰部件列表为空，停止添加
            }
            int index = random.nextInt(interferingPartsList.size());
            String part = interferingPartsList.get(index);
            components.add(new CharacterComponent(part, ""));
            interferingPartsList.remove(index); // 移除已添加的干扰部件
        }

        // 随机打乱部件顺序
        java.util.Collections.shuffle(components);
    }
    private void displayComponents() {
        componentsGrid.removeAllViews();
        componentsGrid.setColumnCount(4);

        for (final CharacterComponent component : components) {
            Button button = new Button(this);
            button.setText(component.getComponent());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 处理部件点击事件
                    handleComponentClick(component, (Button) v);
                }
            });

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 200;
            params.height = 200;
            params.setMargins(10, 10, 10, 10);
            componentsGrid.addView(button, params);
        }
    }

    private void displayTargetPositions(String idiom) {
        targetGrid.removeAllViews();
        targetGrid.setColumnCount(4);

        for (int i = 0; i < idiom.length(); i++) {
            final int position = i;
            Button targetButton = new Button(this);
            targetButton.setText("");
            targetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 处理目标位置点击事件
                    handleTargetClick(position, (Button) v);
                }
            });

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 200;
            params.height = 200;
            params.setMargins(10, 10, 10, 10);
            targetGrid.addView(targetButton, params);
        }
    }

    private void handleComponentClick(CharacterComponent component, Button button) {
        if (component.isUsed()) {
            return;
        }

        // 查找第一个空的目标位置
        for (int i = 0; i < targetGrid.getChildCount(); i++) {
            Button targetButton = (Button) targetGrid.getChildAt(i);
            if (targetButton.getText().toString().isEmpty()) {
                targetButton.setText(component.getComponent());
                targetButton.setTag(component);
                component.setUsed(true);
                button.setEnabled(false);
                checkCompletion();
                return;
            }
        }
    }

    private void handleTargetClick(int position, Button targetButton) {
        // 处理目标位置点击事件
        if (targetButton.getTag() != null) {
            CharacterComponent component = (CharacterComponent) targetButton.getTag();
            component.setUsed(false);
            targetButton.setText("");
            targetButton.setTag(null);

            // 重新启用对应的部件按钮
            for (int i = 0; i < componentsGrid.getChildCount(); i++) {
                Button componentButton = (Button) componentsGrid.getChildAt(i);
                if (componentButton.getText().toString().equals(component.getComponent())) {
                    componentButton.setEnabled(true);
                    break;
                }
            }
        }
    }

    private void checkCompletion() {
        IdiomModel currentIdiom = currentIdioms.get(currentIdiomIndex);
        String idiom = currentIdiom.getIdiom();
        StringBuilder formedIdiom = new StringBuilder();

        // 收集当前已组成的成语
        for (int i = 0; i < targetGrid.getChildCount(); i++) {
            Button targetButton = (Button) targetGrid.getChildAt(i);
            if (targetButton.getTag() != null) {
                CharacterComponent component = (CharacterComponent) targetButton.getTag();
                formedIdiom.append(component.getComponent());
            }
        }

        String formedIdiomStr = formedIdiom.toString();
        // 检查是否组成正确的成语
        if (formedIdiomStr.equals(idiom)) {
            String displayText = idiom + "：" + currentIdiom.getExplanation();
            // 记录已猜对的成语
            idiomDao.recordGuessedIdiom(idiom, currentLevel);
            fullIdiomDisplay.setText(displayText);
            fullIdiomDisplay.setVisibility(View.VISIBLE);
            Toast.makeText(this, "恭喜！你组成了成语：" + idiom, Toast.LENGTH_SHORT).show();
        } else if (formedIdiomStr.length() == idiom.length()) {
            //已填满但错误时提示
            Toast.makeText(this, "拼错了！再试试吧~", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadNextIdiom() {
        currentIdiomIndex = (currentIdiomIndex + 1) % currentIdioms.size();
        loadCurrentIdiom();
    }

    private void updateHintCounter() {
        TextView hintCounter = findViewById(R.id.hint_counter);
        hintCounter.setText("剩余提示次数: " + hintCount);
    }

    //找出所有未被猜出的字，随机选取一个来给出提示
    private void showHint() {
        IdiomModel currentIdiom = currentIdioms.get(currentIdiomIndex);
        String idiom = currentIdiom.getIdiom();

        List<Integer> emptyPositions = new ArrayList<>();
        for (int i = 0; i < targetGrid.getChildCount(); i++) {
            Button targetButton = (Button) targetGrid.getChildAt(i);
            if (targetButton.getText().toString().isEmpty()) {
                emptyPositions.add(i);
            }
        }

        if (!emptyPositions.isEmpty()) {
            Random random = new Random();
            int randomIndex = random.nextInt(emptyPositions.size());
            int position = emptyPositions.get(randomIndex);

            String hintChar = String.valueOf(idiom.charAt(position));
            Button targetButton = (Button) targetGrid.getChildAt(position);
            targetButton.setText(hintChar);

            // 找到对应的部件并标记为已使用
            for (CharacterComponent component : components) {
                if (component.getComponent().equals(hintChar) && !component.isUsed()) {
                    component.setUsed(true);
                    targetButton.setTag(component);

                    for (int j = 0; j < componentsGrid.getChildCount(); j++) {
                        Button componentButton = (Button) componentsGrid.getChildAt(j);
                        if (componentButton.getText().toString().equals(component.getComponent())) {
                            componentButton.setEnabled(false);
                            break;
                        }
                    }
                    break;
                }
            }

            checkCompletion();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }
}