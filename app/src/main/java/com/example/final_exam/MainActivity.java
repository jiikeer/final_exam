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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    private IdiomDao idiomDao;
    private String currentLevel; // 当前难度变量
    private List<IdiomModel> currentIdioms;
    private int currentIdiomIndex = 0;
    private int hintCount = 3;
    private List<CharacterComponent> components;
    private Map<String, List<String>> characterComponentsMap;

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

        // 初始化部件映射表
        initCharacterComponentsMap();
        // 获取难度参数并保存
        currentLevel = getIntent().getStringExtra("level");
        if (currentLevel == null) {
            currentLevel = "primary";
        }


        // 获取难度参数
        String level = getIntent().getStringExtra("level");
        if (level == null) {
            level = "primary"; // 默认难度
        }

        // 加载指定难度的成语
        loadIdioms(level);

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

        // 让下一个按钮任何时候都可点击
        nextButton.setEnabled(true);

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

    // 部件替换（部分组成or谐音替换）
    private void initCharacterComponentsMap() {
        characterComponentsMap = new HashMap<>();
        // 小学
        characterComponentsMap.put("烈", Arrays.asList("列"));
        characterComponentsMap.put("怒", Arrays.asList("奴","心"));
        characterComponentsMap.put("冲", Arrays.asList("中"));
        characterComponentsMap.put("语", Arrays.asList("吾"));
        characterComponentsMap.put("暖", Arrays.asList("日"));
        characterComponentsMap.put("恩", Arrays.asList("因","心"));
        characterComponentsMap.put("积", Arrays.asList("鸡","只","禾"));
        characterComponentsMap.put("累", Arrays.asList("田"));
        characterComponentsMap.put("蒂", Arrays.asList("弟","帝"));
        characterComponentsMap.put("落", Arrays.asList("洛"));
        characterComponentsMap.put("笨", Arrays.asList("本"));
        // 中学
        characterComponentsMap.put("憎", Arrays.asList("曾"));
        characterComponentsMap.put("恙", Arrays.asList("羊"));
        characterComponentsMap.put("涉", Arrays.asList("步"));
        characterComponentsMap.put("非", Arrays.asList("飞"));
        characterComponentsMap.put("厉", Arrays.asList("力","万"));
        characterComponentsMap.put("匠", Arrays.asList("斤"));
        characterComponentsMap.put("料", Arrays.asList("米","斗"));
        // 高中
        characterComponentsMap.put("耿", Arrays.asList("耳","火"));
        characterComponentsMap.put("罪", Arrays.asList("非"));
        characterComponentsMap.put("屡", Arrays.asList("尸","娄"));
        characterComponentsMap.put("快", Arrays.asList("筷"));
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
        parseIdiomToComponents(currentIdiom.getIdiom());

        // 显示部件和目标位置
        displayComponents();
        displayTargetPositions(currentIdiom.getIdiom());
    }

    private void parseIdiomToComponents(String idiom) {
        components = new ArrayList<>();
        Random random = new Random();

        for (char c : idiom.toCharArray()) {
            String character = String.valueOf(c);
            if (characterComponentsMap.containsKey(character)) {
                List<String> charComponents = characterComponentsMap.get(character);
                for (String component : charComponents) {
                    components.add(new CharacterComponent(component, character));
                }
            } else {
                // 默认处理，将单字作为部件
                components.add(new CharacterComponent(character, character));
            }
        }

        // 添加一些干扰部件
        List<String> InterferingParts = Arrays.asList("氵", "扌", "口", "心", "禾", "火", "土", "金");
        int InterferingNumber = Math.max(2, 8 - components.size());
        for (int i = 0; i < InterferingNumber; i++) {
            int index = random.nextInt(InterferingParts.size());
            components.add(new CharacterComponent(InterferingParts.get(index), ""));
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
        // 处理部件点击事件
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
                formedIdiom.append(component.getTargetCharacter());
            }
        }

        // 检查是否组成正确的成语
        if (formedIdiom.toString().equals(idiom)) {
            String displayText = idiom + "：" + currentIdiom.getExplanation();
        // 记录已猜对的成语
            idiomDao.recordGuessedIdiom(idiom, currentLevel);
            fullIdiomDisplay.setText(displayText);
            fullIdiomDisplay.setVisibility(View.VISIBLE); // 显示完整的成语及其提示
            Toast.makeText(this, "恭喜！你组成了成语：" + idiom, Toast.LENGTH_SHORT).show();
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
                if (component.getTargetCharacter().equals(hintChar) && !component.isUsed()) {
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